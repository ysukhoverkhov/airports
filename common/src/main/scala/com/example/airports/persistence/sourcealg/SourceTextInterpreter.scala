package com.example.airports.persistence.sourcealg

import cats.effect.IO
import com.example.airports.domain.ErrorReason
import com.example.airports.persistence.SourceAlg

// Text source for CSV data
class SourceTextInterpreter(text: String) extends SourceAlg[IO] {

  override def source: IO[Either[ErrorReason, String]] = {
    IO.pure(Right(text))
  }
}
