package com.example.airports.persistence

import cats.data.EitherT
import com.example.airports.domain.ErrorReason

// Algebra for data sources
trait SourceAlg[F[_]] {
  def source: EitherT[F, ErrorReason, String]
}
