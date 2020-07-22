package com.jetbrains.plugin.blockmap

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Represents a chunk, returned from the FastCDC iterator.
 * offset - start position within the original content.
 * length - length of the chunk in bytes.
 * hash - chunk hash.
 *
 * Note: two chunks are equals each other if and only if
 * their hashes and lengths are the same but their
 * offsets may be different.
 */
data class Chunk(
  @JsonProperty("hash") val hash: String,
  @JsonProperty("offset") val offset: Int,
  @JsonProperty("length") val length: Int
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    other as Chunk
    if (hash != other.hash) return false
    if (length != other.length) return false
    return true
  }

  override fun hashCode(): Int {
    var result = hash.hashCode()
    result = 31 * result + length
    return result
  }
}
