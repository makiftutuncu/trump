package com.github.makiftutuncu.scalacandidatetest.domain

import com.github.makiftutuncu.scalacandidatetest.domain.models.MaybeF
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.ExecutionContext

trait Cache[A] extends StrictLogging {
  val isEnabled: Boolean
  val defaultTTLInSeconds: Int

  protected def internalGet(key: String): MaybeF[Option[A]]
  protected def internalSet(key: String, value: String, ttl: Int = defaultTTLInSeconds): MaybeF[Unit]

  def get(key: String): MaybeF[Option[A]] =
    if (!isEnabled) {
      logger.debug(s"Not getting key $key from cache, cache is disabled")
      MaybeF.value(None)
    } else {
      internalGet(key)
    }

  def set(key: String, value: String, ttl: Int = defaultTTLInSeconds): MaybeF[Unit] =
    if (!isEnabled) {
      logger.debug(s"Not setting key $key to cache, cache is disabled")
      MaybeF.value(())
    } else {
      internalSet(key, value, ttl)
    }

  def use[B](key: String, ttl: Int = defaultTTLInSeconds)(getNewData: => MaybeF[A])(setNewData: A => String)(action: A => MaybeF[B])(implicit ec: ExecutionContext): MaybeF[B] =
    get(key).flatMap {
      case Left(readError) =>
        logger.error(s"Failed to use cache for key $key: $readError")
        MaybeF.error(readError)

      case Right(Some(dataFromCache)) =>
        action(dataFromCache)

      case Right(None) =>
        if (isEnabled) { logger.debug(s"Missed the cache for key $key") }
        getNewData.flatMap {
          case Left(dataError) =>
            logger.error(s"Failed to get new data while using cache for key $key: $dataError")
            MaybeF.error(dataError)

          case Right(newData) =>
            set(key, setNewData(newData), ttl).flatMap { _ =>
              action(newData)
            }
        }
    }
}
