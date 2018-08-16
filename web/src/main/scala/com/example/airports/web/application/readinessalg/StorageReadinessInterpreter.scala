package com.example.airports.web.application.readinessalg

import cats.effect.IO
import com.example.airports.persistence.MemoryStorageAlg
import com.example.airports.web.application.ReadinessAlg
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{ExecutionContext, Future}

class StorageReadinessInterpreter(
  storage: MemoryStorageAlg[IO])(
  implicit val ec: ExecutionContext) extends ReadinessAlg[IO] with StrictLogging {

  private var ready = false

  Future {
    logger.info("Loading data...")
    storage.data.value.unsafeRunSync()
    ready = true
    logger.info("Loading finished, server ready")
  }

  override def isReady: IO[Boolean] = IO.pure(ready)
}
