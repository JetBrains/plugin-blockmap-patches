package com.jetbrains.plugin.blockmap

import java.io.Serializable

class ChunkWithHash : Serializable {
  companion object {
    private val serialVersionUid: Long = 123
  }
  var hash = ByteArray(0)
  var offset = 0
  var length = 0
  constructor(hash : ByteArray,
              offset : Int, length : Int){
    this.hash = hash
    this.offset = offset
    this.length = length
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as ChunkWithHash

    if (!hash.contentEquals(other.hash)) return false
    if (offset != other.offset) return false
    if (length != other.length) return false

    return true
  }

  override fun hashCode(): Int {
    var result = hash.contentHashCode()
    result = 31 * result + offset
    result = 31 * result + length
    return result
  }
}
