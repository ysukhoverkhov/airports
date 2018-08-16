package com.example.airports.logic.queryalg

import cats._
import cats.data.EitherT
import com.example.airports.domain
import com.example.airports.domain.ErrorReason
import com.example.airports.logic.QueryResult.AirportPerCountry
import com.example.airports.logic.QueryResult.AirportPerCountry.{Airport, Runway}
import com.example.airports.logic.{QueryAlg, QueryResult}
import com.example.airports.persistence.MemoryStorageAlg

class InMemoryQueryInterpreter[F[_]](storage: MemoryStorageAlg[F])(implicit F: Applicative[F]) extends QueryAlg[F] {

  override def airportsPerCountry(query: String): EitherT[F, ErrorReason, AirportPerCountry] = {
    for {
      data <- storage.data
      country = data.countries.find(countryMatch(query))
      airports: Seq[domain.Airport] = country.toList.flatMap(c => data.airports.filter(_.countryCode == c.code))
      resultAirports = airports.map { a =>
        Airport(
          name = a.name,
          runways = data.runways.filter(_.airportId == a.id).map(r => Runway(r.surface))
        )
      }
    } yield {
      QueryResult.AirportPerCountry(resultAirports)
    }
  }

  private def countryMatch(query: String)(country: domain.Country): Boolean = {
    (country.code.toLowerCase == query.toLowerCase) || country.name.toLowerCase.contains(query.toLowerCase)
  }

  override def topCountriesWithAirports(limit: Int): EitherT[F, ErrorReason, QueryResult.CountriesWithAirports] = {
    for {
      data <- storage.data
      airportsByCountry = data.airports.groupBy(_.countryCode).mapValues(_.size)
      sortedCountries = data.countries.sortBy(c => airportsByCountry.getOrElse(c.code, 0))
      selectedCountries = (sortedCountries.take(limit) ++ sortedCountries.takeRight(limit)).reverse
      resultCountries = selectedCountries.map { c =>
        QueryResult.CountriesWithAirports.CountryWithAirportCount(
          code = c.code,
          name = c.name,
          airportsCount = airportsByCountry.getOrElse(c.code, 0)
        )
      }
    } yield {
      QueryResult.CountriesWithAirports(resultCountries)
    }
  }

  override def runwayTypesPerCountry: EitherT[F, ErrorReason, QueryResult.RunwayTypesPerCountry] = {
    for {
      data <- storage.data

      airportsByCountry = data.airports.groupBy(_.countryCode)
      runwaysByAirport = data.runways.groupBy(_.airportId)
      runwaysByCountry = airportsByCountry.mapValues(as => as.map(a => runwaysByAirport.getOrElse(a.id, Nil))).mapValues(_.flatten)
      distinctRunwaysByCountry = runwaysByCountry.mapValues(_.map(_.surface).distinct)

      resultCountries = data.countries.map { c =>
        QueryResult.RunwayTypesPerCountry.CountryWithRunwayType(
          code = c.code,
          name = c.name,
          runwayTypes = distinctRunwaysByCountry.getOrElse(c.code, Nil)
        )
      }

    } yield {
      QueryResult.RunwayTypesPerCountry(resultCountries)
    }
  }
}
