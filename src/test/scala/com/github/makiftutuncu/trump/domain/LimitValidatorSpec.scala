package com.github.makiftutuncu.trump.domain

import org.scalacheck.Gen
import org.scalatest.{MustMatchers, PropSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class LimitValidatorSpec extends PropSpec with GeneratorDrivenPropertyChecks with MustMatchers {
  private val validLimitGenerator = Gen.choose(LimitValidator.minLimit, LimitValidator.maxLimit)

  property("Validation result must be empty for valid limits") {
    forAll(validLimitGenerator -> "limit") { limit: Int =>
      LimitValidator.validate(limit) must be(None)
    }
  }

  property("Validation result must have errors for invalid limits") {
    forAll("limit") { limit: Int =>
      whenever(limit < LimitValidator.minLimit || limit > LimitValidator.maxLimit) {
        LimitValidator.validate(limit) must be(Some(Errors.invalidLimit(limit)))
      }
    }
  }
}
