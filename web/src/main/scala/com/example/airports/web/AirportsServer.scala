package com.example.airports.web

import cats.effect.{Effect, IO}
import com.example.airports.logic.queryalg.InMemoryQueryInterpreter
import com.example.airports.persistence.memorystoragealg.MemoryStorageCSVInterpreter
import com.example.airports.persistence.sourcealg.SourceResourceInterpreter
import com.example.airports.web.application.ApplicationAlg
import com.example.airports.web.application.applicationalg.ApplicationInterpreter
import fs2.{Stream, StreamApp}
import org.http4s.HttpService
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext

object AirportsServer extends StreamApp[IO] {
  import scala.concurrent.ExecutionContext.Implicits.global

  val countriesSource = new SourceResourceInterpreter(Config.CountriesResourceFile)
  val airportsSource = new SourceResourceInterpreter(Config.AirportsResourceFile)
  val runwaysSource = new SourceResourceInterpreter(Config.RunwaysResourceFile)

  val persistence = new MemoryStorageCSVInterpreter(countriesSource, airportsSource, runwaysSource)

  val data = persistence.data.unsafeRunSync().right.get // TODO: ugly hack

  val queryEngine = new InMemoryQueryInterpreter[IO](data)
  val application = new ApplicationInterpreter[IO](queryEngine)

  def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, StreamApp.ExitCode] = ServerStream.stream[IO](application)
}

object ServerStream {

  def stream[F[_]: Effect](application: ApplicationAlg[F])(implicit ec: ExecutionContext): Stream[F, StreamApp.ExitCode] =
    BlazeBuilder[F]
      .bindHttp(8080, "0.0.0.0")
      .mountService(airportsService(application), "/")
      .mountService(staticFilesService, "/")
      .serve

  private def airportsService[F[_]: Effect](application: ApplicationAlg[F]): HttpService[F] = {
    new AirportsService[F](application).service
  }

  private def staticFilesService[F[_]: Effect]: HttpService[F] = {
    new StaticFilesService[F].service
  }
}
