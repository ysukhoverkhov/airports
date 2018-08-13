package com.example.airports.domain

final case class Data(
  countries: Seq[Country],
  airports: Seq[Airport],
  runways: Seq[Runway]
)

final case class Country(
  code: String,
  name: String
)

final case class Airport (
  id: String,
  name: String,
  countryCode: String
)

final case class Runway(
  airportId: String,
  surface: String,
  identifier: String
)
