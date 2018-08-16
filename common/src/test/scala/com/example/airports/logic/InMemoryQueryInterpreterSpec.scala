package com.example.airports.logic

import cats.effect.IO
import com.example.airports.logic.QueryResult.{AirportPerCountry, CountriesWithAirports, RunwayTypesPerCountry}
import com.example.airports.logic.QueryResult.AirportPerCountry.{Airport, Runway}
import com.example.airports.logic.QueryResult.CountriesWithAirports.CountryWithAirportCount
import com.example.airports.logic.QueryResult.RunwayTypesPerCountry.CountryWithRunwayType
import com.example.airports.logic.queryalg.InMemoryQueryInterpreter
import com.example.airports.persistence.memorystoragealg.MemoryStorageCSVInterpreter
import com.example.airports.persistence.sourcealg.SourceTextInterpreter
import org.specs2.mutable.Specification

import scala.concurrent.ExecutionContext.Implicits.global

// These tests are incomplete. In production I'd add following tests
// 1 for airports per country:
// 1.1 Query for country without airports
// 2 Top countries with airports
// 2.1 Query with limit equal to 0
// 2.2 Query with limit * 2 > than total amount of countries
// 3 Runway types per country
// 3.1 Would add more airports with more runways of the same type to make sure there are no duplicates of runway types per country
class InMemoryQueryInterpreterSpec extends Specification {
  "InMemoryQueryInterpreter" >> {
    "airportsPerCountry" >> {
      "with country code" >> {
        val result = queryEngine.airportsPerCountry("AE").value.unsafeRunSync()
        result === expectedAirports
      }

      "wth partial name" >> {
        val result = queryEngine.airportsPerCountry("arab").value.unsafeRunSync()
        result === expectedAirports
      }

      "with not existing country" >> {
        val result = queryEngine.airportsPerCountry("name of the country which does not exist").value.unsafeRunSync()
        result === expectedEmptyAirports
      }
    }

    "topCountriesWithAirports" >> {
      val result = queryEngine.topCountriesWithAirports(1).value.unsafeRunSync()
      result === expectedCountriesWithAirports
    }

    "runwayTypesPerCountry" >> {
      val result = queryEngine.runwayTypesPerCountry.value.unsafeRunSync()
      result === expectedRunwayTypePerCountry
    }
  }

  val countriesSource = new SourceTextInterpreter(
    """
      |"id","code","name","continent","wikipedia_link","keywords"
      |302672,"AD","Andorra","EU","http://en.wikipedia.org/wiki/Andorra",
      |302618,"AE","United Arab Emirates","AS","http://en.wikipedia.org/wiki/United_Arab_Emirates","UAE"
    """.stripMargin)

  val airportsSource = new SourceTextInterpreter(
    """
      |"id","ident","type","name","latitude_deg","longitude_deg","elevation_ft","continent","iso_country","iso_region","municipality","scheduled_service","gps_code","iata_code","local_code","home_link","wikipedia_link","keywords"
      |6523,"00A","heliport","Total Rf Heliport",40.07080078125,-74.93360137939453,11,"NA","AD","US-PA","Bensalem","no","00A",,"00A",,,
      |6524,"00AK","small_airport","Lowell Field",59.94919968,-151.695999146,450,"NA","AE","US-AK","Anchor Point","no","00AK",,"00AK",,,
    """.stripMargin)

  val runwaysSource = new SourceTextInterpreter(
    """
      |"id","airport_ref","airport_ident","length_ft","width_ft","surface","lighted","closed","le_ident","le_latitude_deg","le_longitude_deg","le_elevation_ft","le_heading_degT","le_displaced_threshold_ft","he_ident","he_latitude_deg","he_longitude_deg","he_elevation_ft","he_heading_degT","he_displaced_threshold_ft",
      |269408,6523,"00A",80,80,"ASPH-G",1,0,"H1",,,,,,,,,,,
      |255155,6524,"00AK",2500,70,"GRVL",0,0,"N",,,,,,"S",,,,,
    """.stripMargin)

  val storage = new MemoryStorageCSVInterpreter(countriesSource, airportsSource, runwaysSource)
  val queryEngine = new InMemoryQueryInterpreter[IO](storage)

  val expectedAirports = Right(AirportPerCountry(List(Airport("Lowell Field", List(Runway("GRVL"))))))
  val expectedEmptyAirports = Right(AirportPerCountry(List()))

  val expectedCountriesWithAirports = Right(CountriesWithAirports(List(CountryWithAirportCount("AE", "United Arab Emirates", 1), CountryWithAirportCount("AD", "Andorra", 1))))

  val expectedRunwayTypePerCountry = Right(RunwayTypesPerCountry(List(CountryWithRunwayType("AD", "Andorra", List("ASPH-G")), CountryWithRunwayType("AE", "United Arab Emirates", List("GRVL")))))
}
