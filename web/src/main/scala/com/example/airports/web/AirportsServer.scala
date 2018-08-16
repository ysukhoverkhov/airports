package com.example.airports.web

import cats.effect.{Effect, IO}
import com.example.airports.logic.queryalg.InMemoryQueryInterpreter
import com.example.airports.persistence.memorystoragealg.MemoryStorageCSVInterpreter
import com.example.airports.persistence.sourcealg.SourceResourceInterpreter
import com.example.airports.web.application.{ApplicationAlg, ReadinessAlg}
import com.example.airports.web.application.applicationalg.ApplicationInterpreter
import com.example.airports.web.application.readinessalg.StorageReadinessInterpreter
import fs2.{Stream, StreamApp}
import org.http4s.HttpService
import org.http4s.server.blaze.BlazeBuilder

import scala.concurrent.ExecutionContext

object AirportsServer extends StreamApp[IO] {
  import scala.concurrent.ExecutionContext.Implicits.global

  val countriesSource = new SourceResourceInterpreter(Config.CountriesResourceFile)
  val airportsSource = new SourceResourceInterpreter(Config.AirportsResourceFile)
  val runwaysSource = new SourceResourceInterpreter(Config.RunwaysResourceFile)

  val storage = new MemoryStorageCSVInterpreter(countriesSource, airportsSource, runwaysSource)

  val queryEngine = new InMemoryQueryInterpreter[IO](storage)
  val readiness = new StorageReadinessInterpreter(storage)

  val application = new ApplicationInterpreter[IO](queryEngine, readiness)

  def stream(args: List[String], requestShutdown: IO[Unit]): Stream[IO, StreamApp.ExitCode] =
    ServerStream.stream[IO](application, readiness)
}

object ServerStream {

  def stream[F[_]: Effect](
    application: ApplicationAlg[F],
    readiness: ReadinessAlg[F])(implicit ec: ExecutionContext): Stream[F, StreamApp.ExitCode] =

    BlazeBuilder[F]
      .bindHttp(8080, "0.0.0.0")
      .mountService(readinessService(readiness), "/health")
      .mountService(airportsService(application), "/api")
      .mountService(staticFilesService, "/")
      .serve

  private def airportsService[F[_]: Effect](application: ApplicationAlg[F]): HttpService[F] = {
    new AirportsService[F](application).service
  }

  private def readinessService[F[_]: Effect](readiness: ReadinessAlg[F]): HttpService[F] = {
    new ReadinessService[F](readiness).service
  }

  private def staticFilesService[F[_]: Effect]: HttpService[F] = {
    new StaticFilesService[F].service
  }
}
