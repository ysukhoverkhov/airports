package com.example.airports.web

import cats.implicits._
import cats.effect.Effect
import com.example.airports.web.application.{ApplicationAlg, ReadinessAlg}
import io.circe.Json
import org.http4s.HttpService
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl

// Service for handling UI request
class AirportsService[F[_]: Effect](application: ApplicationAlg[F]) extends Http4sDsl[F] {

  val service: HttpService[F] = {
    HttpService[F] {
      case GET -> Root / "airports" / country =>
        application.airportsPerCountry(country).value.flatMap {
          case Right(r) => Ok(r)

          // Needs better error handling - detect incorrect date format
          case Left(er) => InternalServerError(Json.obj("error" -> Json.fromString(er.reason)))
        }

      case GET -> Root / "report" / "topcountries" =>
        application.topCountriesWithAirports(Config.TopCountriesByAirportsLimit).value.flatMap {
          case Right(r) => Ok(r)

          // Needs better error handling - detect incorrect date format
          case Left(er) => InternalServerError(Json.obj("error" -> Json.fromString(er.reason)))
        }

      case GET -> Root / "report" / "runways" =>
        application.runwayTypesPerCountry.value.flatMap {
          case Right(r) => Ok(r)

          // Needs better error handling - detect incorrect date format
          case Left(er) => InternalServerError(Json.obj("error" -> Json.fromString(er.reason)))
        }
    }
  }
}
