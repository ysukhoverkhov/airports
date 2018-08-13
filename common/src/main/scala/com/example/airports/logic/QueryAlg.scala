package com.example.airports.logic

import com.example.airports.logic.QueryResult._

trait QueryAlg[F[_]] {
  def airportsPerCountry(query: String): F[AirportPerCountry]
  def topCountriesWithAirports: F[CountriesWithAirports]
  def runwayTypesPerCountry: F[RunwayTypesPerCountry]
}
