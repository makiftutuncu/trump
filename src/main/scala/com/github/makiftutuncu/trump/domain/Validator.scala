package com.github.makiftutuncu.trump.domain

trait Validator[A] {
  def validate(a: A): Option[ShoutError]
}
