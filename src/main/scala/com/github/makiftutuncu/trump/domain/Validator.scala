package com.github.makiftutuncu.trump.domain

import com.github.makiftutuncu.trump.domain.models.ShoutError

trait Validator[A] {
  def validate(a: A): Option[ShoutError]
}
