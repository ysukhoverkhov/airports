package com.example.airports.web.application

trait ReadinessAlg[F[_]] {
  def isReady: F[Boolean]
}
