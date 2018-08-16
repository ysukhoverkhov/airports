package com.example.airports.web

import cats.effect.IO
import com.example.airports.web.application.applicationalg.ApplicationInterpreterStub
import org.http4s._
import org.http4s.implicits._
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification

// We need contract testing here (like with pact). But it's out of scope
class AirportsServiceSpec extends Specification {

  "AirportsService" >> {
    "airports for country" >> {
      "return 200" >> {
        uriReturns200("/airports/zim")
      }
    }

    "top countries" >> {
      "return 200" >> {
        uriReturns200("report/topcountries")
      }
    }

    "runways for countries" >> {
      "return 200" >> {
        uriReturns200("report/runways")
      }
    }
  }

  private val application = new ApplicationInterpreterStub[IO]
  private val service = new AirportsService[IO](application).service

  private def httpQuery(uriString: String): Response[IO] = {
    val get = Request[IO](Method.GET, Uri.fromString(uriString).right.get)
    service.orNotFound(get).unsafeRunSync()
  }

  private def uriReturns200(uri: String): MatchResult[Status] =
    httpQuery(uri).status must beEqualTo(Status.Ok)
}
