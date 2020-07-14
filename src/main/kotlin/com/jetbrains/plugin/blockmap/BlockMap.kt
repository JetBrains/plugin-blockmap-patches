package com.jetbrains.plugin.blockmap

import java.io.*
import kotlin.collections.ArrayList


class BlockMap : Serializable{
  companion object {
    private const val serialVersionUID: Long = 1234567
  }
  val chunks: ArrayList<FastCDC.Chunk> = ArrayList()
  var algorithm : String private set
  /**
   * @param source the source for which the blockmap will be created.
   * @param algorithm the name of the algorithm requested. See the MessageDigest java class.
   */
  constructor(source : InputStream, algorithm : String){
    this.algorithm = algorithm
    val fastCDC = FastCDC(source, algorithm)
    for(chunk in fastCDC){
      chunks.add(chunk)
    }
  }

  constructor(source : InputStream) : this(source, "SHA-256")

  /**
   * Compare this and another blockmaps and return all chunks contained in another but not contained in this.
   */
  fun compare(another: BlockMap) : ArrayList<FastCDC.Chunk>{
    val oldHashMap = HashMap<String, FastCDC.Chunk>()
    for(chunk in this.chunks){
      if(!oldHashMap.contains(chunk.hash)){
        oldHashMap[chunk.hash] = chunk
      }
    }
    val result = ArrayList<FastCDC.Chunk>()
    for(newChunk in another.chunks){
      if(!oldHashMap.contains(newChunk.hash)){
        result.add(newChunk)
      }
    }
    return result
  }

}











