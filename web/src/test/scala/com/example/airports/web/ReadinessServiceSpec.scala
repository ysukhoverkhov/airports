package com.example.airports.web

import cats.effect.IO
import com.example.airports.web.application.readinessalg.ConstReadinessInterpreter
import org.http4s
import org.http4s.implicits._
import org.http4s.{Method, Request, Response, Status, Uri}
import org.specs2.matcher.MatchResult
import org.specs2.mutable.Specification

class ReadinessServiceSpec extends Specification {

  "ReadinessService" >> {
    "alive" >> {
      "return 200 for ready app" >> {
        uriReturnsStatusForService("/alive", Status.Ok, readyService)
      }

      "return 200 for NOT ready app" >> {
        uriReturnsStatusForService("/alive", Status.Ok, notReadyService)
      }
    }

    "ready" >> {
      "return 200 for ready app" >> {
        uriReturnsStatusForService("/ready", Status.Ok, readyService)
      }

      "return 503 for NOT ready app" >> {
        uriReturnsStatusForService("/ready", Status.ServiceUnavailable, notReadyService)
      }
    }
  }

  private val readyReadiness = new ConstReadinessInterpreter[IO](true)
  private val notReadyReadiness = new ConstReadinessInterpreter[IO](false)
  private val readyService = new ReadinessService[IO](readyReadiness).service
  private val notReadyService = new ReadinessService[IO](notReadyReadiness).service

  private def httpQueryWithService(uriString: String, service: http4s.HttpService[IO]): Response[IO] = {
    val get = Request[IO](Method.GET, Uri.fromString(uriString).right.get)
    service.orNotFound(get).unsafeRunSync()
  }

  private def uriReturnsStatusForService(uri: String, status: Status, service: http4s.HttpService[IO]): MatchResult[Status] =
    httpQueryWithService(uri, service).status must beEqualTo(status)
}
