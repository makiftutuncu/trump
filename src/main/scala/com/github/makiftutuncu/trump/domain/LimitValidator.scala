package com.github.makiftutuncu.trump.domain

import com.github.makiftutuncu.trump.Config.Limits

class LimitValidator(val limits: Limits) extends Validator[Int] {
  override def validate(limit: Int): Option[ShoutError] =
    if (limit < limits.min || limit > limits.max) {
      Some(Errors.invalidLimit(limit))
    } else {
      None
    }
}
