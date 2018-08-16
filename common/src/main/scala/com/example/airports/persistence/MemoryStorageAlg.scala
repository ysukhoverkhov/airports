package com.example.airports.persistence

import cats.data.EitherT
import com.example.airports.domain.{Data, ErrorReason}

// Algebra for fetching inventory form a storage
trait MemoryStorageAlg[F[_]] {
  def data: EitherT[F, ErrorReason, Data]
}
