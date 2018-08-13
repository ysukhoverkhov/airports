package com.example.airports.web.application.applicationalg

import cats.Monad
import cats.implicits._
import com.example.airports.domain.ErrorReason
import com.example.airports.logic.{QueryAlg, QueryResult}
import com.example.airports.web.application.ApplicationAlg
import io.circe._
import io.circe.syntax._


class ApplicationInterpreter[F[_]](queryEngine: QueryAlg[F])(implicit F: Monad[F]) extends ApplicationAlg[F] {

  override def airportsPerCountry(query: String): F[Either[ErrorReason, Json]] = {
    val queryResult = queryEngine.airportsPerCountry(query)
    val json = queryResult.map(composeResult[QueryResult.AirportPerCountry])
    json.map(_.asRight)
  }

  implicit private val encAirportPerCountry: Encoder[QueryResult.AirportPerCountry] = (a: QueryResult.AirportPerCountry) => {
    Json.obj("airports" -> a.airports.asJson)
  }

  implicit private val encAirport: Encoder[QueryResult.AirportPerCountry.Airport] = (a: QueryResult.AirportPerCountry.Airport) => {
    Json.obj("name" -> a.name.asJson)
  }


  override def topCountriesWithAirports: F[Either[ErrorReason, Json]] = {
    val queryResult = queryEngine.topCountriesWithAirports
    val json = queryResult.map(composeResult[QueryResult.CountriesWithAirports])
    json.map(_.asRight)
  }

  implicit private val encCountriesWithAirports: Encoder[QueryResult.CountriesWithAirports] = (a: QueryResult.CountriesWithAirports) => {
    Json.obj("countries" -> a.countries.asJson)
  }

  implicit private val encCountryWithAirports: Encoder[QueryResult.CountriesWithAirports.CountryWithAirportCount] = (a: QueryResult.CountriesWithAirports.CountryWithAirportCount) => {
    Json.obj("code" -> a.code.asJson, "name" -> a.name.asJson, "airportsCount" -> a.airportsCount.asJson)
  }


  override def runwayTypesPerCountry: F[Either[ErrorReason, Json]] = {
    val queryResult = queryEngine.runwayTypesPerCountry
    val json = queryResult.map(composeResult[QueryResult.RunwayTypesPerCountry])
    json.map(_.asRight)
  }

  implicit private val encCountriesWithRunways: Encoder[QueryResult.RunwayTypesPerCountry] = (a: QueryResult.RunwayTypesPerCountry) => {
    Json.obj("countries" -> a.countries.asJson)
  }

  implicit private val encCountryWithRunways: Encoder[QueryResult.RunwayTypesPerCountry.CountryWithRunwayType] = (a: QueryResult.RunwayTypesPerCountry.CountryWithRunwayType) => {
    Json.obj("code" -> a.code.asJson, "name" -> a.name.asJson, "runways" -> a.runwayTypes.asJson)
  }

  private def composeResult[T](queryResult: T)(implicit encoder: Encoder[T]): Json = queryResult.asJson
}
