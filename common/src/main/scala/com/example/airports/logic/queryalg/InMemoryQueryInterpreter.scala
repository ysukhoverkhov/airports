package com.example.airports.logic.queryalg

import cats._
import com.example.airports.domain
import com.example.airports.domain.Data
import com.example.airports.logic.QueryResult.AirportPerCountry.{Airport, Runway}
import com.example.airports.logic.{QueryAlg, QueryResult}

// TODO: write tests for me
class InMemoryQueryInterpreter[F[_]](data: Data)(implicit F: Applicative[F]) extends QueryAlg[F] {

  override def airportsPerCountry(query: String): F[QueryResult.AirportPerCountry] = {
    val country = data.countries.find(countryMatch(query))
    val airports: Seq[domain.Airport] = country.toList.flatMap(c => data.airports.filter(_.countryCode == c.code))
    val resultAirports = airports.map { a =>
      Airport(
        name = a.name,
        runways = data.runways.filter(_.airportId == a.id).map(r => Runway(r.surface))
      )
    }
    F.pure(QueryResult.AirportPerCountry(resultAirports))
  }

  private def countryMatch(query: String)(country: domain.Country): Boolean = {
    (country.code.toLowerCase == query.toLowerCase) || country.name.toLowerCase.contains(query.toLowerCase)
  }

  override def topCountriesWithAirports: F[QueryResult.CountriesWithAirports] = {
    val airportsByCountry = data.airports.groupBy(_.countryCode).mapValues(_.size)
    val sortedCountries = data.countries.sortBy(c => airportsByCountry.getOrElse(c.code, 0))
    val selectedCountries = (sortedCountries.take(10) ++ sortedCountries.takeRight(10)).reverse
    val resultCountries = selectedCountries.map { c =>
      QueryResult.CountriesWithAirports.CountryWithAirportCount(
        code = c.code,
        name = c.name,
        airportsCount = airportsByCountry.getOrElse(c.code, 0)
      )
    }

    F.pure(QueryResult.CountriesWithAirports(resultCountries))
  }

  override def runwayTypesPerCountry: F[QueryResult.RunwayTypesPerCountry] = {
    val airportsByCountry = data.airports.groupBy(_.countryCode)
    val runwaysByAirport = data.runways.groupBy(_.airportId)
    val runwaysByCountry = airportsByCountry.mapValues(as => as.map(a => runwaysByAirport.getOrElse(a.id, Nil))).mapValues(_.flatten)
    val distinctRunwaysByCountry = runwaysByCountry.mapValues(_.map(_.surface).distinct)

    val resultCountries = data.countries.map { c =>
      QueryResult.RunwayTypesPerCountry.CountryWithRunwayType(
        code = c.code,
        name = c.name,
        runwayTypes = distinctRunwaysByCountry.getOrElse(c.code, Nil)
      )
    }

    F.pure(QueryResult.RunwayTypesPerCountry(resultCountries))
  }
}
