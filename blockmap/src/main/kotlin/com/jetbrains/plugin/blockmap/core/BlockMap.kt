package com.jetbrains.plugin.blockmap.core

import java.io.*

internal const val ALGORITHM = "SHA-256"

private const val NORMAL_SIZE = 8 * 1024

private const val MIN_SIZE = NORMAL_SIZE / 4

private const val MAX_SIZE = NORMAL_SIZE * 8

data class BlockMap(
  val chunks: List<Chunk> = listOf(),
  val algorithm: String = ALGORITHM,
  val minSize: Int = MIN_SIZE,
  val maxSize: Int = MAX_SIZE,
  val normalSize: Int = NORMAL_SIZE
) {
  /**
   * @param source the source for which the blockmap will be created.
   * @param algorithm the name of the algorithm requested. See the MessageDigest java class.
   *
   */
  constructor(source: InputStream, algorithm: String = ALGORITHM) :
    this(FastCDCImpl(source, algorithm, MIN_SIZE, MAX_SIZE, NORMAL_SIZE).asSequence().toList(), algorithm,
      MIN_SIZE, MAX_SIZE, NORMAL_SIZE)

  constructor(source: InputStream, algorithm: String, minSize: Int, maxSize: Int, normalSize: Int) :
    this(FastCDCImpl(source, algorithm, minSize, maxSize, normalSize).asSequence().toList(), algorithm,
      minSize, maxSize, normalSize)
}
