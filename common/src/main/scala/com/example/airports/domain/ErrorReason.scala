package com.example.airports.domain

final case class ErrorReason private (reason: String)

// Error container for the project. In a bigger one it would be nice to have it as Algebraic DT
object ErrorReason {

  def fromThrowable(throwable: Throwable): ErrorReason = {
    ErrorReason(s"$throwable")
  }

  def fromThrowable(reason: String)(throwable: Throwable): ErrorReason = {
    ErrorReason(s"$reason: $throwable")
  }

  def fromString(reason: String): ErrorReason = {
    ErrorReason(reason)
  }
}
