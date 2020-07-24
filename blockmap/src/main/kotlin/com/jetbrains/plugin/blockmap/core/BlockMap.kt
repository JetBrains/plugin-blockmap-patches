package com.jetbrains.plugin.blockmap.core

import java.io.*

internal const val ALGORITHM = "SHA-256"

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
    this(FastCDCImpl(source, algorithm).asSequence().toList(), algorithm)
}
