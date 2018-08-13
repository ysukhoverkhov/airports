package com.example.airports.web.application

import com.example.airports.domain.ErrorReason
import io.circe.Json

// Application algebra, reflects served requests (one so far)
trait ApplicationAlg[F[_]] {
  def airportsPerCountry(country: String): F[Either[ErrorReason, Json]]
  def topCountriesWithAirports: F[Either[ErrorReason, Json]]
  def runwayTypesPerCountry: F[Either[ErrorReason, Json]]
}
