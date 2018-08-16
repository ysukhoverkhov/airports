package com.example.airports.persistence.sourcealg

import cats.data.EitherT
import cats.effect.IO
import cats.implicits._
import com.example.airports.domain.ErrorReason
import com.example.airports.persistence.SourceAlg

// Resource source for CSV data
class SourceResourceInterpreter(resource: String) extends SourceAlg[IO] {

  private val classLoader = Thread.currentThread().getContextClassLoader

  override def source: EitherT[IO, ErrorReason, String] = {
    EitherT {
      IO {
        Either.catchNonFatal {
          scala.io.Source.fromResource(resource, classLoader).mkString
        }.leftMap(ErrorReason.fromThrowable(s"Error loading resource [$resource]"))
      }
    }
  }
}
