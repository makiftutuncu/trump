package com.github.makiftutuncu.trump.domain

import io.circe.{Encoder, Json}

trait Cache[A] {
  val defaultTTLInSeconds: Int

  def get(key: String): MaybeF[Option[A]]
  def set(key: String, value: Json, ttl: Int = defaultTTLInSeconds): MaybeF[Unit]
  def use[B](key: String, ttl: Int = defaultTTLInSeconds)(getNewData: => MaybeF[A])(action: A => MaybeF[B])(implicit encoder: Encoder[A]): MaybeF[B]
}
