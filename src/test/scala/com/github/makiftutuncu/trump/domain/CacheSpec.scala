package com.github.makiftutuncu.scalacandidatetest.domain

import com.github.makiftutuncu.scalacandidatetest.MockCache
import com.github.makiftutuncu.scalacandidatetest.domain.models.{Maybe, MaybeF}
import org.scalatest.{BeforeAndAfterEach, MustMatchers, WordSpec}

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global

class CacheSpec extends WordSpec with MustMatchers with BeforeAndAfterEach {
  "Getting value from cache" must {
    "return None when cache is disabled" in {
      disabledCache.map += ("foo" -> (disabledCache.now, 1000, "bar"))
      Await.result(disabledCache.get("foo"), Duration.Inf) must be(Maybe.value(None))
    }

    "return None when key is not found in cache" in {
      Await.result(cache.get("foo"), Duration.Inf) must be(Maybe.value(None))
    }

    "return None when value is expired" in {
      cache.map += ("foo" -> (0L, 1000, "bar"))
      Await.result(cache.get("foo"), Duration.Inf) must be(Maybe.value(None))
    }

    "return value properly when it is not expired" in {
      cache.map += ("foo" -> (cache.now, 1000, "bar"))
      Await.result(cache.get("foo"), Duration.Inf) must be(Maybe.value(Some("bar")))
    }
  }

  "Setting value to cache" must {
    "not set value when cache is disabled" in {
      Await.result(disabledCache.set("foo", "bar"), Duration.Inf) must be(Maybe.value(()))
      disabledCache.map.get("foo")                                must be(None)
    }

    "set value properly with default ttl" in {
      Await.result(cache.set("foo", "bar"), Duration.Inf) must be(Maybe.value(()))
      val (time, ttl, value) = cache.map.getOrElse("foo", (0L, 0, ""))
      time  must be(cache.now)
      ttl   must be(cache.defaultTTLInSeconds)
      value must be("bar")
    }

    "set value properly with custom ttl" in {
      Await.result(cache.set("foo", "bar", 500), Duration.Inf) must be(Maybe.value(()))
      val (time, ttl, value) = cache.map.getOrElse("foo", (0L, 0, ""))
      time  must be(cache.now)
      ttl   must be(500)
      value must be("bar")
    }
  }

  "Using cache" must {
    "use value from cache when it is available" in {
      cache.map += ("foo" -> (cache.now, cache.defaultTTLInSeconds, "bar"))
      Await.result(cache.use("foo")(MaybeF.value("baz"))(identity)(v => MaybeF.value(v.toUpperCase)), Duration.Inf) must be(Maybe.value("BAR"))
    }

    "get new value, set it to cache and use that one" in {
      Await.result(cache.use("foo")(MaybeF.value("baz"))(identity)(v => MaybeF.value(v.toUpperCase)), Duration.Inf) must be(Maybe.value("BAZ"))
      val (time, ttl, value) = cache.map.getOrElse("foo", (0L, 0, ""))
      time  must be(cache.now)
      ttl   must be(cache.defaultTTLInSeconds)
      value must be("baz")
    }
  }

  private val disabledCache: MockCache = new MockCache(isEnabled = false)
  private val cache: MockCache         = new MockCache()

  override protected def afterEach(): Unit = {
    super.afterEach()
    disabledCache.map = Map.empty
    cache.map         = Map.empty
  }
}
