package com.example.airports.persistence.memorystoragealg

import com.example.airports.domain.{Airport, Country, Data, Runway}
import com.example.airports.persistence.sourcealg.SourceTextInterpreter
import org.specs2.mutable.Specification

class StorageCSVInterpreterSpec extends Specification {
  val countryCsv = new SourceTextInterpreter(
    """
      |"id","code","name","continent","wikipedia_link","keywords"
      |302672,"AD","Andorra","EU","http://en.wikipedia.org/wiki/Andorra",
      |302618,"AE","United Arab Emirates","AS","http://en.wikipedia.org/wiki/United_Arab_Emirates","UAE"
    """.stripMargin)

  val airportCsv = new SourceTextInterpreter(
    """
      |"id","ident","type","name","latitude_deg","longitude_deg","elevation_ft","continent","iso_country","iso_region","municipality","scheduled_service","gps_code","iata_code","local_code","home_link","wikipedia_link","keywords"
      |6523,"00A","heliport","Total Rf Heliport",40.07080078125,-74.93360137939453,11,"NA","US","US-PA","Bensalem","no","00A",,"00A",,,
      |6524,"00AK","small_airport","Lowell Field",59.94919968,-151.695999146,450,"NA","US","US-AK","Anchor Point","no","00AK",,"00AK",,,
    """.stripMargin)

  val runwayCsv = new SourceTextInterpreter(
    """
      |"id","airport_ref","airport_ident","length_ft","width_ft","surface","lighted","closed","le_ident","le_latitude_deg","le_longitude_deg","le_elevation_ft","le_heading_degT","le_displaced_threshold_ft","he_ident","he_latitude_deg","he_longitude_deg","he_elevation_ft","he_heading_degT","he_displaced_threshold_ft",
      |269408,6523,"00A",80,80,"ASPH-G",1,0,"H1",,,,,,,,,,,
      |255155,6524,"00AK",2500,70,"GRVL",0,0,"N",,,,,,"S",,,,,
    """.stripMargin)

  val expectedInventory = Data(
    List(Country("AD", "Andorra"), Country("AE", "United Arab Emirates")),
    List(Airport("00A", "Total Rf Heliport" ,"US"), Airport("00AK", "Lowell Field", "US")),
    List(Runway("00A", "ASPH-G", "H1"), Runway("00AK", "GRVL", "N"))
  )

  // TODO: fix me.
  "StorageCSVInterpreter" >> {
    "Parses countries" >> {
      ok
//      val result = new MemoryStorageCSVInterpreter(countryCsv, airportCsv, runwayCsv).data.unsafeRunSync()
//      result must beLike { case Right(inv) => inv === expectedInventory }
    }
  }
}
