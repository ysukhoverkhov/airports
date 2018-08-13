package com.example.airports.persistence.sourcealg

import org.scalacheck.Prop
import org.specs2.ScalaCheck
import org.specs2.mutable.Specification

class CsvSourceTextInterpreterSpec extends Specification with ScalaCheck {

  "CsvSourceTextInterpreter" >> {
    "Always returns passed text" >> {
      Prop.forAll((a: String) => new SourceTextInterpreter(a).source.unsafeRunSync() == Right(a))
    }
  }
}
