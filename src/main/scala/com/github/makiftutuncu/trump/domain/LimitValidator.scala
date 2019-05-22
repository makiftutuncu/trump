package com.github.makiftutuncu.trump.domain

object LimitValidator extends Validator[Int] {
  val minLimit: Int = 1
  val maxLimit: Int = 10

  override def validate(limit: Int): List[ShoutError] =
    if (limit < minLimit || limit > maxLimit) {
      List(Errors.invalidLimit(limit))
    } else {
      List.empty
    }
}
