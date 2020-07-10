package com.jetbrains.plugin.blockmap

import com.jetbrains.plugin.blockmap.protocol.PluginBlockMapDescriptorRequest
import com.jetbrains.plugin.blockmap.protocol.PluginBlockMapDescriptorResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import java.io.*
import java.security.MessageDigest

class PluginBlockMapCreator (private val s3Client: S3Client){
  companion object {
    private val logger: Logger = LoggerFactory.getLogger(PluginBlockMapCreator::class.java)
  }

  fun createPluginBlockMap(request: PluginBlockMapDescriptorRequest): PluginBlockMapDescriptorResponse {
    logger.info("Downloading file from S3")
    val updateFileKey = getKeyFromPath(request.bucketPrefix, request.key)
    val inputStream = s3Client.getObject { getObjectRequest ->
      getObjectRequest
        .bucket(request.bucketName)
        .key(updateFileKey)
    }
    logger.info("File downloaded")

    val source = inputStream.readBytes()
    val blockMapFilePath = getBlockMapFilePath(updateFileKey)

    s3Client.putObject({putObjectRequest ->
      putObjectRequest
        .bucket(request.bucketName)
        .key(blockMapFilePath)
    }, RequestBody.fromBytes(createBlockMapBytes(source)))
    logger.info("BlockMap file uploaded")
    return PluginBlockMapDescriptorResponse(blockMapFilePath)
  }

  fun createBlockMapBytes(source : ByteArray) : ByteArray{
    val fastCDC = FastCDC(source)
    val hashedChunks = getHashedChunks(source, fastCDC)
    return getBlockMapBytes(hashedChunks)
  }

  private fun getBlockMapFilePath(key : String) : String{
    val list = key.split("/")
    var result = ""
    for(i in 0 until list.size-1){
      result+=list[i]+"/"
    }
    // TODO: Add blockmap file name to configure properties
    result+="blockmap.bin"
    return result
  }

  private fun getBlockMapBytes(hashedChunks : ArrayList<ChunkWithHash>) : ByteArray{
    val outBytes = ByteArrayOutputStream()
    val outObjects = ObjectOutputStream(outBytes)
    for(chunk in hashedChunks){
      outObjects.writeObject(chunk)
    }
    outObjects.flush()
    outObjects.close()
    return outBytes.toByteArray()
  }

  private fun getHashedChunks(source : ByteArray, fastCDC: FastCDC) : ArrayList<ChunkWithHash>{
    val result = ArrayList<ChunkWithHash>()
    for(chunk in fastCDC){
      if(chunk != null){
        val hash = getSHA256Hash(source, chunk)
        result.add(ChunkWithHash(hash, chunk.offset, chunk.length))
      }
    }
    return result
  }

  private fun getSHA256Hash(source : ByteArray, chunk : FastCDC.Chunk) : ByteArray{
    val digest = MessageDigest.getInstance("SHA-256")
    val chunkBytes = ByteArray(chunk.length)
    for(i in 0 until chunk.length){
      chunkBytes[i] = source[i+chunk.offset]
    }
    return digest.digest(chunkBytes)
  }

  private fun getKeyFromPath(bucketPrefix: String, filePath: String): String {
    return if (bucketPrefix.isEmpty()) filePath else "$bucketPrefix/${filePath.removePrefix("/")}"
  }

}


