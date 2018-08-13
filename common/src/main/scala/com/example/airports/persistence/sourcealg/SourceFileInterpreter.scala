package com.example.airports.persistence.sourcealg

import cats.effect.IO
import cats.implicits._
import com.example.airports.domain.ErrorReason
import com.example.airports.persistence.SourceAlg

// File source for CSV data
class SourceFileInterpreter(filePath: String) extends SourceAlg[IO] {

  override def source: IO[Either[ErrorReason, String]] = {
    IO {
      Either.catchNonFatal {
        scala.io.Source.fromFile(filePath).mkString
      }.leftMap(ErrorReason.fromThrowable)
    }
  }
}
