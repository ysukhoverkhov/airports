package com.example.airports.persistence

import com.example.airports.domain.{Data, ErrorReason}

// Algebra for fetching inventory form a storage
trait MemoryStorageAlg[F[_]] {
  def data: F[Either[ErrorReason, Data]]
}
