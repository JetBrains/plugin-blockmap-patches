package com.jetbrains.plugin.blockmap.core

import java.io.*
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.*


private const val ALGORITHM = "SHA-256"

data class BlockMap(
  val chunks: List<Chunk> = listOf(),
  val algorithm: String = ALGORITHM
) {
  /**
   * @param source the source for which the blockmap will be created.
   * @param algorithm the name of the algorithm requested. See the MessageDigest java class.
   *
   */
  constructor(source: InputStream, algorithm: String = ALGORITHM) :
    this(FastCDC(source, algorithm).asSequence().toList(), algorithm)
}

fun makeFileHash(source: InputStream, algorithm: String = ALGORITHM): String {
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












