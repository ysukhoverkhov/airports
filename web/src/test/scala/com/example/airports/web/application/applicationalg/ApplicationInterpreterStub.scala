package com.example.airports.web.application.applicationalg

import cats.Monad
import cats.implicits._
import com.example.airports.domain.ErrorReason
import com.example.airports.web.application.ApplicationAlg
import io.circe._
import io.circe.syntax._

class ApplicationInterpreterStub[F[_]](implicit F: Monad[F]) extends ApplicationAlg[F] {

  private val response: F[Either[ErrorReason, Json]] = F.pure(Json.obj("I'm" -> "Kinda result".asJson).asRight[ErrorReason])

  override def airportsPerCountry(country: String): F[Either[ErrorReason, Json]] = response
  override def topCountriesWithAirports: F[Either[ErrorReason, Json]] = response
  override def runwayTypesPerCountry: F[Either[ErrorReason, Json]] = response
}
