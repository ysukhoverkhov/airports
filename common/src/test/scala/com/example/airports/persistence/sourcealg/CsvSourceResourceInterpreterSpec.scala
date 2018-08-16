package com.example.airports.persistence.sourcealg

import org.specs2.mutable.Specification

class CsvSourceResourceInterpreterSpec extends Specification {
  val ExistingResource = "test_inventory.csv"
  val NotExistingResource = "test_inventory_which_does_not_exist.csv"

  "CsvSourceResourceInterpreter" >> {
    "Returns existing resource" >> {
      new SourceResourceInterpreter(ExistingResource).source.value.unsafeRunSync() must beRight
    }

    "Does not return not existing resource" >> {
      new SourceResourceInterpreter(NotExistingResource).source.value.unsafeRunSync() must beLeft
    }
  }
}
