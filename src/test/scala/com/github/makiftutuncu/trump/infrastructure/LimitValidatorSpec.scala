package com.github.makiftutuncu.scalacandidatetest.infrastructure

import com.github.makiftutuncu.scalacandidatetest.Config.Limits
import com.github.makiftutuncu.scalacandidatetest.domain.models.Errors
import org.scalacheck.Gen
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import org.scalatest.{MustMatchers, PropSpec}

class LimitValidatorSpec extends PropSpec with GeneratorDrivenPropertyChecks with MustMatchers {
  private val limits              = Limits(1, 10)
  private val limitValidator      = new LimitValidator(limits)
  private val validLimitGenerator = Gen.choose(limits.min, limits.max)

  property("Validation result must be empty for valid limits") {
    forAll(validLimitGenerator -> "limit") { limit: Int =>
      limitValidator.validate(limit) must be(None)
    }
  }

  property("Validation result must have errors for invalid limits") {
    forAll("limit") { limit: Int =>
      whenever(limit < limits.min || limit > limits.max) {
        limitValidator.validate(limit) must be(Some(Errors.invalidLimit(limit, limits.min, limits.max)))
      }
    }
  }
}
