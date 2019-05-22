package com.github.makiftutuncu.trump.domain

object LimitValidator extends Validator[Int] {
  val minLimit: Int = 1
  val maxLimit: Int = 10

  override def validate(limit: Int): Option[ShoutError] =
    if (limit < minLimit || limit > maxLimit) {
      Some(Errors.invalidLimit(limit))
    } else {
      None
    }
}
