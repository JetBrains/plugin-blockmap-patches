package com.jetbrains.plugin.blockmap

import java.io.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.*


@Serializable
class BlockMap private constructor() {
  companion object {
    fun fromJson(json: String): BlockMap {
      return Json(JsonConfiguration.Stable).parse(serializer(), json)
    }
  }

  /**
   * @param source the source for which the blockmap will be created.
   * @param algorithm the name of the algorithm requested. See the MessageDigest java class.
   */
  constructor(source: InputStream, algorithm: String = "SHA-256") : this() {
    chunks = FastCDC(source, algorithm).asSequence().toList()
    this.algorithm = algorithm
  }

  private var algorithm: String = "SHA-256"
  var chunks: List<FastCDC.Chunk> = Collections.emptyList()
    private set

  /**
   * Compare this and another blockmaps and return all chunks contained in another but not contained in this.
   */
  fun compare(another: BlockMap): List<FastCDC.Chunk> {
    val oldSet = chunks.toSet()
    return another.chunks.filter { chunk -> !oldSet.contains(chunk) }.toList()
  }

  fun toJson(): String {
    val json = Json(JsonConfiguration.Stable)
    return json.stringify(serializer(), this)
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












