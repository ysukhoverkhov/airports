package com.example.airports.web.application.readinessalg

import cats.Applicative
import com.example.airports.web.application.ReadinessAlg

class ConstReadinessInterpreter[F[_]] (result: Boolean) (implicit F: Applicative[F]) extends ReadinessAlg[F] {
  override def isReady: F[Boolean] = F.pure(result)
}
