package com.jetbrains.plugin.blockmap

import java.io.*
import java.util.stream.Collectors.toMap
import kotlin.collections.ArrayList
import kotlin.streams.toList

/**
 * @param source the source for which the blockmap will be created.
 * @param algorithm the name of the algorithm requested. See the MessageDigest java class.
 */
class BlockMap(
  source: InputStream,
  private val algorithm: String = "SHA-256"
) : Serializable {
  companion object {
    private const val serialVersionUID: Long = 1234567
  }

  val chunks: List<FastCDC.Chunk> = FastCDC(source, algorithm).asSequence().toList()

  /**
   * Compare this and another blockmaps and return all chunks contained in another but not contained in this.
   */
  fun compare(another: BlockMap): List<FastCDC.Chunk> {
    val oldHashMap = chunks.associate { it.hash to chunks }
    return another.chunks.filter { chunk -> !oldHashMap.containsKey(chunk.hash) }.toList()
  }

}











