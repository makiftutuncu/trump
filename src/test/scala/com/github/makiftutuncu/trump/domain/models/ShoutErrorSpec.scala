package com.github.makiftutuncu.scalacandidatetest.domain.models

import io.circe.Json
import io.circe.syntax._
import org.scalatest.{MustMatchers, WordSpec}

class ShoutErrorSpec extends WordSpec with MustMatchers {
  "A ShoutError" must {
    "be converted to Json properly" in {
      ShoutError(1, "test-error").asJson                 must be(Json.obj("error" -> "test-error".asJson, "details" -> Json.Null))
      ShoutError(2, "test-error", "test-details").asJson must be(Json.obj("error" -> "test-error".asJson, "details" -> "test-details".asJson))
    }

    "be converted to String properly" in {
      ShoutError(1, "test-error").toString                 must be("test-error")
      ShoutError(2, "test-error", "test-details").toString must be("test-error: test-details")
    }
  }
}
