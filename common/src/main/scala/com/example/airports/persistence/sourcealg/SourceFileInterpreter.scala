package com.example.airports.persistence.sourcealg

import cats.data.EitherT
import cats.effect.IO
import cats.implicits._
import com.example.airports.domain.ErrorReason
import com.example.airports.persistence.SourceAlg

// File source for CSV data
class SourceFileInterpreter(filePath: String) extends SourceAlg[IO] {

  override def source: EitherT[IO, ErrorReason, String] = {
    EitherT {
      IO {
        Either.catchNonFatal {
          scala.io.Source.fromFile(filePath).mkString
        }.leftMap(ErrorReason.fromThrowable)
      }
    }
  }
}
