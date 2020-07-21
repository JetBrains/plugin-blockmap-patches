package com.jetbrains.plugin.blockmap

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jetbrains.plugin.blockmap.protocol.PluginBlockMapDescriptorRequest
import com.jetbrains.plugin.blockmap.protocol.PluginBlockMapDescriptorResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client


class PluginBlockMapCreator(private val s3Client: S3Client) {
  companion object {
    private val logger: Logger = LoggerFactory.getLogger(PluginBlockMapCreator::class.java)
    private val mapper = jacksonObjectMapper()
    // TODO: Add blockmap file name to configure properties
    private const val blockMapFileName = "blockmap.json"
  }

  fun createPluginBlockMap(request: PluginBlockMapDescriptorRequest): PluginBlockMapDescriptorResponse {
    val updateFileKey = getKeyFromPath(request.bucketPrefix, request.key)
    logger.info("Downloading file $updateFileKey from S3")
    val inputStream = s3Client.getObject { getObjectRequest ->
      getObjectRequest
        .bucket(request.bucketName)
        .key(updateFileKey)
    }
    logger.info("File $updateFileKey downloaded")

    val blockMapFilePath = getBlockMapFilePath(updateFileKey, blockMapFileName)
    logger.info("Creating blockmap")

    val blockMap = inputStream.use { BlockMap(inputStream) }
    logger.info("Blockmap created")

    logger.info("Uploading blockmap file $blockMapFilePath")
    s3Client.putObject({ putObjectRequest ->
      putObjectRequest
        .bucket(request.bucketName)
        .key(blockMapFilePath)
    }, RequestBody.fromString(mapper.writeValueAsString(blockMap)))
    logger.info("Blockmap file $blockMapFilePath uploaded")

    return PluginBlockMapDescriptorResponse(blockMapFilePath)
  }


  private fun getBlockMapFilePath(filePath: String, blockMapFileName: String): String {
    return filePath.replaceAfterLast("/", blockMapFileName, blockMapFileName)
  }

  private fun getKeyFromPath(bucketPrefix: String, filePath: String): String {
    return if (bucketPrefix.isEmpty()) filePath else "$bucketPrefix/${filePath.removePrefix("/")}"
  }

}
