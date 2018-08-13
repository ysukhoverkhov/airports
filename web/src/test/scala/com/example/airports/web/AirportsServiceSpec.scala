package com.example.airports.web

import cats.effect.IO
import com.example.airports.web.application.applicationalg.ApplicationInterpreterStub
import org.http4s._
import org.http4s.implicits._
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification

// We need contract testing here (like with pact). But it's out of scope
class AirportsServiceSpec extends Specification {

  "Airports" >> {
    "return 200 for correct date" >> {
      uriReturns200()
    }
  }

  private val application = new ApplicationInterpreterStub[IO]
  private val service = new AirportsService[IO](application).service

  private val retQuery: Response[IO] = {
    val get = Request[IO](Method.GET, Uri.uri("/airports/zim"))
    service.orNotFound(get).unsafeRunSync()
  }

  private def uriReturns200(): MatchResult[Status] =
    retQuery.status must beEqualTo(Status.Ok)
}
