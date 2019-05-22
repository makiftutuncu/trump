package com.github.makiftutuncu.trump.domain

import org.scalacheck.Prop.{BooleanOperators, forAll}
import org.scalacheck.{Gen, Properties}

class LimitValidatorSpec extends Properties("LimitValidator") {
  private val validLimitGenerator = Gen.choose(LimitValidator.minLimit, LimitValidator.maxLimit)

  property("validLimits") = forAll(validLimitGenerator) { limit: Int =>
    LimitValidator.validate(limit).isEmpty :| s"$limit is not a valid limit"
  }

  property("invalidLimits") = forAll { limit: Int =>
    (limit < LimitValidator.minLimit || limit > LimitValidator.maxLimit) ==>
      LimitValidator.validate(limit).nonEmpty :| s"$limit is a valid limit"
  }
}
