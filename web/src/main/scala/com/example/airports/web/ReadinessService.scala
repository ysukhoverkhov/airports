package com.example.airports.web

import cats.effect.Effect
import cats.implicits._
import com.example.airports.web.application.ReadinessAlg
import org.http4s.HttpService
import org.http4s.dsl.Http4sDsl


// Service for handling UI request
class ReadinessService[F[_]: Effect](readiness: ReadinessAlg[F]) extends Http4sDsl[F] {

  val service: HttpService[F] = {
    HttpService[F] {
      case GET -> Root / "alive" =>
        Ok("OK")

      case GET -> Root / "ready" =>
        readiness.isReady.flatMap {
          case true => Ok("OK")
          case false => ServiceUnavailable("NOT READY")
        }
    }
  }
}
