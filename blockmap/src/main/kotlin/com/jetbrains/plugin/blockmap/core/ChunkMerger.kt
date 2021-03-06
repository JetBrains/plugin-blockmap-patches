package com.jetbrains.plugin.blockmap.core

import java.io.*

open class ChunkMerger(
  private val oldFile: File,
  private val oldBlockMap: BlockMap,
  private val newBlockMap: BlockMap,
  private val bufferSize: Int = 64 * 1024
) {
  private val buffer = ByteArray(bufferSize)

  /**
   * Restore a new file from old file chunks and new file chunks by
   * merging old and new chunks.
   * @param output stream where restored file will be written.
   * @param newChunkDataSource source of new chunks where chunks go in the
   * same order as they are in the difference between new and old chunk sets.
   * For example if a new file consists of chunks: old1, old2, new3, old3, new2, new1 then
   * [newChunkDataSource] must consists of chunks data new3, new2, new1 etc (in that order).
   */
  @Throws(IOException::class)
  open fun merge(output: OutputStream, newChunkDataSource: Iterator<ByteArray>) {
    RandomAccessFile(oldFile, "r").use { oldFileRAF ->
      output.buffered().use { bufferedOutput ->
        val oldMap = oldBlockMap.chunks.associateBy { it.hash }
        for (newChunk in newBlockMap.chunks) {
          val oldChunk = oldMap[newChunk.hash]
          if (oldChunk != null) downloadChunkFromOldData(oldChunk, oldFileRAF, bufferedOutput)
          else downloadChunkFromNewData(newChunk, newChunkDataSource, bufferedOutput)
        }
      }
    }
  }

  @Throws(IOException::class)
  open fun downloadChunkFromOldData(
    oldChunk: Chunk, oldFileRAF: RandomAccessFile, output: OutputStream
  ) {
    oldFileRAF.seek(oldChunk.offset.toLong())
    var remainingBytes = oldChunk.length
    while (remainingBytes != 0) {
      val length = if (remainingBytes >= bufferSize) bufferSize else remainingBytes
      oldFileRAF.read(buffer, 0, length)
      output.write(buffer, 0, length)
      remainingBytes -= length
    }
  }

  @Throws(IOException::class)
  open fun downloadChunkFromNewData(
    newChunk: Chunk, newChunkDataSource: Iterator<ByteArray>, output: OutputStream
  ) {
    if (newChunkDataSource.hasNext()) {
      val chunkData = newChunkDataSource.next()
      if (chunkData.size == newChunk.length) {
        output.write(chunkData)
      } else throw IOException("Received chunk has wrong length: " +
        "${chunkData.size} but expected ${newChunk.length}.")
    } else throw IOException("New chunks data iterator hasn't got enough chunks.")
  }
}
