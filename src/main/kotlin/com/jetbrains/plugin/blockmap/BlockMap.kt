package com.jetbrains.plugin.blockmap


import java.io.*
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.*


class BlockMap private constructor() {
  /**
   * @param source the source for which the blockmap will be created.
   * @param algorithm the name of the algorithm requested. See the MessageDigest java class.
   *
   * To avoid serialization problem due to source and don't serialize source
   * we use that constructor with private default constructor
   */
  constructor(source: InputStream, algorithm: String = "SHA-256") : this() {
    this.chunks = FastCDC(source, algorithm).asSequence().toList()
    this.algorithm = algorithm
  }

  var algorithm: String = "SHA-256"
    private set
  var chunks: List<Chunk> = Collections.emptyList()
    private set


  /**
   * Compare this and another blockmaps and return all chunks contained in another but not contained in this.
   */
  fun compare(another: BlockMap): List<Chunk> {
    val oldSet = chunks.toSet()
    return another.chunks.filter { chunk -> !oldSet.contains(chunk) }.toList()
  }
}

fun makeFileHash(source: InputStream, algorithm: String = "SHA-256"): String {
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












