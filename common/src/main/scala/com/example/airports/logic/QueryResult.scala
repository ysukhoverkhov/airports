package com.example.airports.logic

import com.example.airports.logic.QueryResult.AirportPerCountry.Airport
import com.example.airports.logic.QueryResult.CountriesWithAirports.CountryWithAirportCount
import com.example.airports.logic.QueryResult.RunwayTypesPerCountry.CountryWithRunwayType

object QueryResult {
  case class AirportPerCountry(airports: Seq[Airport])
  object AirportPerCountry {
    case class Airport(name: String, runways: Seq[Runway])
    case class Runway(surface: String)
  }

  case class CountriesWithAirports(countries: Seq[CountryWithAirportCount])
  object CountriesWithAirports {
    case class CountryWithAirportCount(code: String, name: String, airportsCount: Int)
  }

  case class RunwayTypesPerCountry(countries: Seq[CountryWithRunwayType])
  object RunwayTypesPerCountry {
    case class CountryWithRunwayType(code: String, name: String, runwayTypes: Seq[String])
  }
}
