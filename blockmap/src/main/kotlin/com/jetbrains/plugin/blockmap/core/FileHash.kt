package com.jetbrains.plugin.blockmap.core

import java.io.InputStream
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.*

data class FileHash(val hash: String = "", val algorithm: String = "") {
  constructor(source: InputStream, algorithm: String = ALGORITHM) : this(makeFileHash(source, algorithm), algorithm)

  companion object {
    fun makeFileHash(source: InputStream, algorithm: String): String {
      val digest = MessageDigest.getInstance(algorithm)
      source.buffered().use { input ->
        DigestInputStream(input, digest).use { digestInputStream ->
          val buffer = ByteArray(1024 * 8)
          while (digestInputStream.read(buffer) != -1) {
          }
        }
      }
      return Base64.getEncoder().encodeToString(digest.digest())
    }
  }

}

