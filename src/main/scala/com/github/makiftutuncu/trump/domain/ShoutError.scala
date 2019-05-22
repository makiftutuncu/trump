package com.github.makiftutuncu.trump.domain

import io.circe.Encoder

case class ShoutError(code: Int, error: String, details: Option[String])

object ShoutError {
  implicit val shoutErrorEncoder: Encoder[ShoutError] =
    Encoder.forProduct2("error", "details") { se =>
      (se.error, se.details)
    }

  def apply(code: Int, error: String): ShoutError                  = new ShoutError(code, error, None)
  def apply(code: Int, error: String, details: String): ShoutError = new ShoutError(code, error, Some(details))
}
