package com.jetbrains.plugin.blockmap

import java.io.*
import java.util.stream.Collectors.toMap
import kotlin.collections.ArrayList
import kotlin.streams.toList


class BlockMap : Serializable {
  companion object {
    private const val serialVersionUID: Long = 1234567
  }

  var chunks: ArrayList<FastCDC.Chunk> = ArrayList()
  var algorithm: String private set

  /**
   * @param source the source for which the blockmap will be created.
   * @param algorithm the name of the algorithm requested. See the MessageDigest java class.
   */
  constructor(source: InputStream, algorithm: String) {
    this.algorithm = algorithm
    FastCDC(source, algorithm).forEach { chunk -> chunks.add(chunk) }
  }

  constructor(source: InputStream) : this(source, "SHA-256")

  /**
   * Compare this and another blockmaps and return all chunks contained in another but not contained in this.
   */
  fun compare(another: BlockMap): List<FastCDC.Chunk> {
    val oldHashMap = chunks.stream()
      .collect(toMap<FastCDC.Chunk, String, FastCDC.Chunk>(
        { chunk -> chunk.hash }, { chunk -> chunk })
      )
    return another.chunks.stream().filter { chunk -> !oldHashMap.containsKey(chunk.hash) }.toList()
  }

}











