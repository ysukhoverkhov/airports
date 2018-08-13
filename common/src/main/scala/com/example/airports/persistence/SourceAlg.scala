package com.example.airports.persistence

import com.example.airports.domain.ErrorReason

// Algebra for data sources
trait SourceAlg[F[_]] {
  def source: F[Either[ErrorReason, String]]
}
