package com.example.airports.web.application.applicationalg

import cats.Monad
import cats.data.EitherT
import cats.implicits._
import com.example.airports.domain.ErrorReason
import com.example.airports.logic.{QueryAlg, QueryResult}
import com.example.airports.web.application.{ApplicationAlg, ReadinessAlg}
import io.circe._
import io.circe.syntax._


class ApplicationInterpreter[F[_]](
  queryEngine: QueryAlg[F],
  readiness: ReadinessAlg[F])(implicit F: Monad[F]) extends ApplicationAlg[F] {

  override def airportsPerCountry(query: String): EitherT[F, ErrorReason, Json] = {
    runQuery[QueryResult.AirportPerCountry](queryEngine.airportsPerCountry(query))
  }

  override def topCountriesWithAirports: EitherT[F, ErrorReason, Json] = {
    runQuery[QueryResult.CountriesWithAirports](queryEngine.topCountriesWithAirports)
  }

  override def runwayTypesPerCountry: EitherT[F, ErrorReason, Json] = {
    runQuery[QueryResult.RunwayTypesPerCountry](queryEngine.runwayTypesPerCountry)
  }

  private def runQuery[T](query: => EitherT[F, ErrorReason, T])(implicit encoder: Encoder[T]): EitherT[F, ErrorReason, Json] = {
    for {
      _ <- checkReadiness
      result <- query.map(composeResult[T])
    } yield result
  }

  private def checkReadiness: EitherT[F, ErrorReason, Unit] = {
    EitherT {
      readiness.isReady.map {
        case true  => ().asRight
        case false => ErrorReason("Not ready yet").asLeft
      }
    }
  }

  private def composeResult[T](queryResult: T)(implicit encoder: Encoder[T]): Json = queryResult.asJson


  implicit private val encAirportPerCountry: Encoder[QueryResult.AirportPerCountry] = (a: QueryResult.AirportPerCountry) => {
    Json.obj("airports" -> a.airports.asJson)
  }

  implicit private val encAirport: Encoder[QueryResult.AirportPerCountry.Airport] = (a: QueryResult.AirportPerCountry.Airport) => {
    Json.obj("name" -> a.name.asJson)
  }

  implicit private val encCountriesWithAirports: Encoder[QueryResult.CountriesWithAirports] = (a: QueryResult.CountriesWithAirports) => {
    Json.obj("countries" -> a.countries.asJson)
  }

  implicit private val encCountryWithAirports: Encoder[QueryResult.CountriesWithAirports.CountryWithAirportCount] = (a: QueryResult.CountriesWithAirports.CountryWithAirportCount) => {
    Json.obj("code" -> a.code.asJson, "name" -> a.name.asJson, "airportsCount" -> a.airportsCount.asJson)
  }

  implicit private val encCountriesWithRunways: Encoder[QueryResult.RunwayTypesPerCountry] = (a: QueryResult.RunwayTypesPerCountry) => {
    Json.obj("countries" -> a.countries.asJson)
  }

  implicit private val encCountryWithRunways: Encoder[QueryResult.RunwayTypesPerCountry.CountryWithRunwayType] = (a: QueryResult.RunwayTypesPerCountry.CountryWithRunwayType) => {
    Json.obj("code" -> a.code.asJson, "name" -> a.name.asJson, "runways" -> a.runwayTypes.asJson)
  }
}
