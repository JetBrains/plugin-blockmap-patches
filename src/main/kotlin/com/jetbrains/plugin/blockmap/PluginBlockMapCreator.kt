package com.jetbrains.plugin.blockmap

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jetbrains.plugin.blockmap.protocol.PluginBlockMapDescriptorRequest
import com.jetbrains.plugin.blockmap.protocol.PluginBlockMapDescriptorResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import java.io.InputStream


class PluginBlockMapCreator(private val s3Client: S3Client) {
  companion object {
    private val logger: Logger = LoggerFactory.getLogger(PluginBlockMapCreator::class.java)
    private val mapper = jacksonObjectMapper()

    const val BLOCKMAP_FILENAME = "blockmap.json"
    const val HASH_FILENAME = "hash.txt"
  }

  fun createPluginBlockMap(request: PluginBlockMapDescriptorRequest): PluginBlockMapDescriptorResponse {
    val updateFileKey = getKeyFromPath(request.bucketPrefix, request.key)
    val bucketName = request.bucketName

    logger.info("Creating blockmap")
    val blockMap = getFileInputStream(bucketName, updateFileKey).use { input -> BlockMap(input) }
    logger.info("Blockmap created")

    val blockMapFilePath = getNewFilePath(updateFileKey, BLOCKMAP_FILENAME)
    logger.info("Uploading blockmap file $blockMapFilePath")
    putStringToBucket(bucketName, blockMapFilePath, mapper.writeValueAsString(blockMap))
    logger.info("Blockmap file $blockMapFilePath uploaded")

    logger.info("Creating plugin hash")
    val pluginHash = getFileInputStream(bucketName, updateFileKey).use { input -> makeFileHash(input) }
    logger.info("Plugin hash created")

    val pluginHashPath = getNewFilePath(updateFileKey, HASH_FILENAME)
    logger.info("Uploading plugin hash file $pluginHashPath")
    putStringToBucket(bucketName, pluginHashPath, pluginHash)
    logger.info("Plugin hash file $pluginHashPath uploaded")

    return PluginBlockMapDescriptorResponse(blockMapFilePath)
  }

  private fun putStringToBucket(bucketName: String, filePath: String, data: String) {
    s3Client.putObject({ putObjectRequest ->
      putObjectRequest
        .bucket(bucketName)
        .key(filePath)
    }, RequestBody.fromString(data))
  }

  private fun getFileInputStream(bucketName: String, filePath: String): InputStream {
    return s3Client.getObject { getObjectRequest ->
      getObjectRequest
        .bucket(bucketName)
        .key(filePath)
    }
  }

  private fun getNewFilePath(oldFilePath: String, newFileName: String): String {
    return oldFilePath.replaceAfterLast("/", newFileName, newFileName)
  }

  private fun getKeyFromPath(bucketPrefix: String, filePath: String): String {
    return if (bucketPrefix.isEmpty()) filePath else "$bucketPrefix/${filePath.removePrefix("/")}"
  }

}
