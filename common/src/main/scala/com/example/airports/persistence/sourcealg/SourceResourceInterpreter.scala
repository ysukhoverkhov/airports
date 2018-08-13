package com.example.airports.persistence.sourcealg

import cats.effect.IO
import cats.implicits._
import com.example.airports.domain.ErrorReason
import com.example.airports.persistence.SourceAlg

// Resource source for CSV data
class SourceResourceInterpreter(resource: String) extends SourceAlg[IO] {

  override def source: IO[Either[ErrorReason, String]] = {
    IO {
      Either.catchNonFatal {
        scala.io.Source.fromResource(resource).mkString
      }.leftMap(ErrorReason.fromThrowable(s"Error loading resource [$resource]"))
    }
  }
}
