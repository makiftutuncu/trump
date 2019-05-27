package com.github.makiftutuncu.scalacandidatetest.infrastructure

import com.github.makiftutuncu.scalacandidatetest.Config.Limits
import com.github.makiftutuncu.scalacandidatetest.domain.Validator
import com.github.makiftutuncu.scalacandidatetest.domain.models.{Errors, ShoutError}

class LimitValidator(val limits: Limits) extends Validator[Int] {
  override def validate(limit: Int): Option[ShoutError] =
    if (limit < limits.min || limit > limits.max) {
      Some(Errors.invalidLimit(limit, limits.min, limits.max))
    } else {
      None
    }
}
