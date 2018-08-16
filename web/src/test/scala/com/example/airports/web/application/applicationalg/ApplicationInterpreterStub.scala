package com.example.airports.web.application.applicationalg

import cats.Monad
import cats.data.EitherT
import cats.implicits._
import com.example.airports.domain.ErrorReason
import com.example.airports.web.application.ApplicationAlg
import io.circe._
import io.circe.syntax._

class ApplicationInterpreterStub[F[_]](implicit F: Monad[F]) extends ApplicationAlg[F] {

  private val response: EitherT[F, ErrorReason, Json] = EitherT(F.pure(Json.obj("I'm" -> "Kinda result".asJson).asRight[ErrorReason]))

  override def airportsPerCountry(country: String): EitherT[F, ErrorReason, Json] = response
  override def topCountriesWithAirports(limit: Int): EitherT[F, ErrorReason, Json] = response
  override def runwayTypesPerCountry: EitherT[F, ErrorReason, Json] = response
}
