package com.github.makiftutuncu.trump.domain

import io.circe.{Json, Printer}
import io.circe.syntax._
import org.scalatest.{MustMatchers, WordSpec}

class ShoutErrorSpec extends WordSpec with MustMatchers {
  private val jsonPrinter            = Printer.noSpaces.copy(dropNullValues = true)
  private def print(j: Json): String = j.pretty(jsonPrinter)

  "A ShoutError" must {
    "be converted to Json properly with no details" in {
      val expected = print(Json.obj("error" -> "test-error".asJson))
      val actual   = print(ShoutError(1, "test-error").asJson)

      actual must be(expected)
    }

    "be converted to Json properly with details" in {
      val expected = print(Json.obj("error" -> "test-error".asJson, "details" -> "test-details".asJson))
      val actual   = print(ShoutError(1, "test-error", "test-details").asJson)

      actual must be(expected)
    }
  }
}
