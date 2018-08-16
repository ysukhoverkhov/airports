package com.example.airports.web.application

import cats.data.EitherT
import com.example.airports.domain.ErrorReason
import io.circe.Json

// Application algebra, reflects served requests (one so far)
trait ApplicationAlg[F[_]] {
  def airportsPerCountry(country: String): EitherT[F, ErrorReason, Json]
  def topCountriesWithAirports(limit: Int): EitherT[F, ErrorReason, Json]
  def runwayTypesPerCountry: EitherT[F, ErrorReason, Json]
}
