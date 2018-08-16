package com.example.airports.logic

import cats.data.EitherT
import com.example.airports.domain.ErrorReason
import com.example.airports.logic.QueryResult._

trait QueryAlg[F[_]] {
  def airportsPerCountry(query: String): EitherT[F, ErrorReason, AirportPerCountry]
  def topCountriesWithAirports: EitherT[F, ErrorReason, CountriesWithAirports]
  def runwayTypesPerCountry: EitherT[F, ErrorReason, RunwayTypesPerCountry]
}
