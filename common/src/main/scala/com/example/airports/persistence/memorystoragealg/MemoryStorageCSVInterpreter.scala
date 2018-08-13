package com.example.airports.persistence.memorystoragealg

import cats.{Eval, Monad}
import cats.data.EitherT
import cats.implicits._
import com.example.airports.domain.{ErrorReason, _}
import com.example.airports.persistence.{MemoryStorageAlg, SourceAlg}
import zamblauskas.csv.parser._

// Storage implementation for CSV data sources
class MemoryStorageCSVInterpreter[F[_]: Monad](
  countries: SourceAlg[F],
  airports: SourceAlg[F],
  runways: SourceAlg[F]) extends MemoryStorageAlg[F] {

  override def data: F[Either[ErrorReason, Data]] = {
    (for {
      countries <- parse[CountryInCsv, Country](countries, parseCountry)
      airports <- parse[AirportInCsv, Airport](airports, parseAirport)
      runways <- parse[RunwayInCsv, Runway](runways, parseRunway)
    } yield {
      Data(countries, airports, runways)
    }).value
  }

  private def parse[I, O](src: SourceAlg[F], parser: I => O)(implicit cr: ColumnReads[I]): EitherT[F, ErrorReason, Seq[O]] = {
    val rv = src.source.map { csv =>
      for {
        csv <- csv
        entries <- Parser.parse[I](csv).leftMap(e => ErrorReason.fromString(reason = s"${e.message} at ${e.lineNum}"))
      } yield {
        entries.map(parser)
      }
    }

    EitherT(rv)
  }

  private case class CountryInCsv(code: String, name: String)
  private def parseCountry(entry: CountryInCsv): Country = {
    Country(
      code = entry.code,
      name = entry.name
    )
  }

  private case class AirportInCsv(ident: String, name: String, iso_country: String)
  private def parseAirport(entry: AirportInCsv): Airport = {
    Airport(
      id = entry.ident,
      name = entry.name,
      countryCode = entry.iso_country
    )
  }

  private case class RunwayInCsv(airport_ident: String, surface: String, le_ident: String)
  private def parseRunway(entry: RunwayInCsv): Runway = {
    Runway(
      airportId = entry.airport_ident,
      surface = entry.surface,
      identifier = entry.le_ident
    )
  }
}
