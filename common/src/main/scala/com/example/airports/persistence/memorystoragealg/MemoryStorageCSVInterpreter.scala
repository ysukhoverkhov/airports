package com.example.airports.persistence.memorystoragealg

import java.util.concurrent.atomic.AtomicBoolean

import cats.data.EitherT
import cats.effect.IO
import cats.implicits._
import com.example.airports.domain.{ErrorReason, _}
import com.example.airports.persistence.{MemoryStorageAlg, SourceAlg}
import zamblauskas.csv.parser._

import scala.concurrent.{ExecutionContext, Promise}

// Storage implementation for CSV data sources
class MemoryStorageCSVInterpreter(
  countries: SourceAlg[IO],
  airports: SourceAlg[IO],
  runways: SourceAlg[IO])(
    implicit ec: ExecutionContext
  ) extends MemoryStorageAlg[IO] {

  private val triggered = new AtomicBoolean(false)
  private val dataCache = Promise[Either[ErrorReason, Data]]()

  override def data: EitherT[IO, ErrorReason, Data] = {
    EitherT {
      IO.suspend {
        if (!triggered.getAndSet(true)) {
          dataCache.completeWith(dataFetch.value.unsafeToFuture)
        }
        IO.fromFuture[Either[ErrorReason, Data]](IO(dataCache.future))
      }
    }
  }

  private def dataFetch: EitherT[IO, ErrorReason, Data] = {
    for {
      countries <- parse[CountryInCsv, Country](countries, parseCountry)
      airports <- parse[AirportInCsv, Airport](airports, parseAirport)
      runways <- parse[RunwayInCsv, Runway](runways, parseRunway)
    } yield {
      Data(countries, airports, runways)
    }
  }

  private def parse[I, O](src: SourceAlg[IO], parser: I => O)(implicit cr: ColumnReads[I]): EitherT[IO, ErrorReason, Seq[O]] = {
    for {
      csv <- src.source
      entries <- EitherT.fromEither[IO](Parser.parse[I](csv).leftMap(e => ErrorReason.fromString(reason = s"${e.message} at ${e.lineNum}")))
    } yield {
      entries.map(parser)
    }
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
