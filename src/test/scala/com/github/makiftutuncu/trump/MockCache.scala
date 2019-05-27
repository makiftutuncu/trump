package com.github.makiftutuncu.scalacandidatetest

import com.github.makiftutuncu.scalacandidatetest.domain.Cache
import com.github.makiftutuncu.scalacandidatetest.domain.models.MaybeF

class MockCache(val now: Long = System.currentTimeMillis,
                override val isEnabled: Boolean = true,
                override val defaultTTLInSeconds: Int = 1000) extends Cache[String] {
  var map: Map[String, (Long, Int, String)] = Map.empty

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
