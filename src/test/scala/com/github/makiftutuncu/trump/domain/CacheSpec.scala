package com.github.makiftutuncu.scalacandidatetest.domain

import com.github.makiftutuncu.scalacandidatetest.domain.models.MaybeF
import org.scalatest.{BeforeAndAfterEach, MustMatchers, WordSpec}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

class CacheSpec extends WordSpec with MustMatchers with BeforeAndAfterEach {
  "Getting value from cache" must {
    "return None when cache is disabled" in {
      map += ("foo" -> (now, 1000, "bar"))
      Await.result(disabledCache.get("foo"), Duration.Inf) must be(Right(None))
    }

    "return None when key is not found in cache" in {
      Await.result(cache.get("foo"), Duration.Inf) must be(Right(None))
    }

    "return None when value is expired" in {
      map += ("foo" -> (0L, 1000, "bar"))
      Await.result(cache.get("foo"), Duration.Inf) must be(Right(None))
    }

    "return value properly when it is not expired" in {
      map += ("foo" -> (now, 1000, "bar"))
      Await.result(cache.get("foo"), Duration.Inf) must be(Right(Some("bar")))
    }
  }

  "Setting value to cache" must {
    "not set value when cache is disabled" in {
      Await.result(disabledCache.set("foo", "bar"), Duration.Inf) must be(Right(()))
      map.get("foo")                                              must be(None)
    }

    "set value properly with default ttl" in {
      Await.result(cache.set("foo", "bar"), Duration.Inf) must be(Right(()))
      val (time, ttl, value) = map.getOrElse("foo", (0L, 0, ""))
      time  must be(now)
      ttl   must be(1000)
      value must be("bar")
    }

    "set value properly with custom ttl" in {
      Await.result(cache.set("foo", "bar", 500), Duration.Inf) must be(Right(()))
      val (time, ttl, value) = map.getOrElse("foo", (0L, 0, ""))
      time  must be(now)
      ttl   must be(500)
      value must be("bar")
    }
  }

  "Using cache" must {
    "use value from cache when it is available" in {
      map += ("foo" -> (now, 1000, "bar"))
      Await.result(cache.use("foo")(MaybeF.value("baz"))(identity)(v => MaybeF.value(v.toUpperCase)), Duration.Inf) must be(Right("BAR"))
    }

    "get new value, set it to cache and use that one" in {
      Await.result(cache.use("foo")(MaybeF.value("baz"))(identity)(v => MaybeF.value(v.toUpperCase)), Duration.Inf) must be(Right("BAZ"))
      val (time, ttl, value) = map.getOrElse("foo", (0L, 0, ""))
      time  must be(now)
      ttl   must be(1000)
      value must be("baz")
    }
  }

  private val now: Long = System.currentTimeMillis

  private var map: Map[String, (Long, Int, String)] = Map.empty

  private val disabledCache: Cache[String] =
    new Cache[String] {
      override val isEnabled: Boolean       = false
      override val defaultTTLInSeconds: Int = 0

      override protected def internalGet(key: String): MaybeF[Option[String]]                = ???
      override protected def internalSet(key: String, value: String, ttl: Int): MaybeF[Unit] = ???
    }

  private val cache: Cache[String] =
    new Cache[String] {
      override val isEnabled: Boolean       = true
      override val defaultTTLInSeconds: Int = 1000

      override protected def internalGet(key: String): MaybeF[Option[String]] =
        MaybeF.value {
          map.get(key).collect {
            case (time, ttl, v) if (time + (ttl * 1000)) > now => v
          }
        }

      override protected def internalSet(key: String, value: String, ttl: Int): MaybeF[Unit] =
        MaybeF.value {
          map += (key -> (now, ttl, value))
        }
    }

  override protected def afterEach(): Unit = {
    super.afterEach()
    map = Map.empty
  }
}
