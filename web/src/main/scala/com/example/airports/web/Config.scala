package com.example.airports.web


// In production this would be loaded from config file
object Config {
  val CountriesResourceFile = "countries.csv"
  val AirportsResourceFile = "airports.csv"
  val RunwaysResourceFile = "runways.csv"

  val TopCountriesByAirportsLimit = 10
}
