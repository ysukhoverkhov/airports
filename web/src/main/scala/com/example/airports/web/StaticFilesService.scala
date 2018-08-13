package com.example.airports.web

import cats.effect.Effect
import org.http4s.dsl.Http4sDsl
import org.http4s.{HttpService, Request, StaticFile}

// Service for handling statics. Dumb and naive one.
class StaticFilesService[F[_]: Effect] extends Http4sDsl[F] {

  val service: HttpService[F] = {
    HttpService[F] {
      case request @ GET -> Root => static ("index.html", request)
      case request @ GET -> Root / path => static (path, request)
    }
  }

  private def static(file: String, request: Request[F]) =
    StaticFile.fromResource("/" + file, Some(request)).getOrElseF(NotFound())
}
