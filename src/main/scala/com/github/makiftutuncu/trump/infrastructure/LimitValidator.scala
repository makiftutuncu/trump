package com.github.makiftutuncu.trump.infrastructure

import com.github.makiftutuncu.trump.Config.Limits
import com.github.makiftutuncu.trump.domain.Validator
import com.github.makiftutuncu.trump.domain.models.{Errors, ShoutError}

class LimitValidator(val limits: Limits) extends Validator[Int] {
  override def validate(limit: Int): Option[ShoutError] =
    if (limit < limits.min || limit > limits.max) {
      Some(Errors.invalidLimit(limit, limits.min, limits.max))
    } else {
      None
    }
}
