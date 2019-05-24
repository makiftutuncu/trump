package com.github.makiftutuncu.scalacandidatetest.domain

import com.github.makiftutuncu.scalacandidatetest.domain.models.ShoutError

trait Validator[A] {
  def validate(a: A): Option[ShoutError]
}
