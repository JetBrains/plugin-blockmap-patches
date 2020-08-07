package com.jetbrains.plugin.blockmap

import com.fasterxml.jackson.databind.ObjectMapper
import com.jetbrains.plugin.blockmap.core.BlockMap
import com.jetbrains.plugin.blockmap.core.FileHash
import com.jetbrains.plugin.blockmap.protocol.PluginBlockMapDescriptorRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


class PluginBlockMapCreator(private val s3Client: S3Client) {
  companion object {
    private val logger: Logger = LoggerFactory.getLogger(PluginBlockMapCreator::class.java)
    private val mapper = ObjectMapper()

    const val BLOCKMAP_FILENAME = "blockmap.json"
    const val BLOCKMAP_ZIP_SUFFIX = "-blockmap.zip"
    const val HASH_FILENAME_SUFFIX = "-hash.json"
  }

  fun createPluginBlockMap(request: PluginBlockMapDescriptorRequest) {
    val updateFileKey = getKeyFromPath(request.bucketPrefix, request.key)
    val bucketName = request.bucketName

    logger.info("Creating blockmap")
    val blockMap = getFileInputStream(bucketName, updateFileKey).use { input -> BlockMap(input) }
    logger.info("Blockmap created")

    val blockMapFilePath = getNewFilePath(updateFileKey, BLOCKMAP_ZIP_SUFFIX)
    logger.info("Uploading blockmap file $blockMapFilePath")
    val zipBytes = createBlockMapZipBytes(mapper.writeValueAsBytes(blockMap))
    putBytesToBucket(bucketName, blockMapFilePath, zipBytes)
    logger.info("Blockmap file $blockMapFilePath uploaded")

    logger.info("Creating plugin hash")
    val pluginHash = getFileInputStream(bucketName, updateFileKey).use { input -> FileHash(input) }
    logger.info("Plugin hash created")

    val pluginHashPath = getNewFilePath(updateFileKey, HASH_FILENAME_SUFFIX)
    logger.info("Uploading plugin hash file $pluginHashPath")
    putStringToBucket(bucketName, pluginHashPath, mapper.writeValueAsString(pluginHash))
    logger.info("Plugin hash file $pluginHashPath uploaded")
  }

  private fun putStringToBucket(bucketName: String, filePath: String, data: String) {
    s3Client.putObject({ putObjectRequest ->
      putObjectRequest
        .bucket(bucketName)
        .key(filePath)
    }, RequestBody.fromString(data))
  }

  private fun putBytesToBucket(bucketName: String, filePath: String, data: ByteArray) {
    s3Client.putObject({ putObjectRequest ->
      putObjectRequest
        .bucket(bucketName)
        .key(filePath)
    }, RequestBody.fromBytes(data))
  }

  private fun createBlockMapZipBytes(bytes: ByteArray): ByteArray {
    ByteArrayOutputStream().use { buffer ->
      ZipOutputStream(buffer).use { output ->
        val entry = ZipEntry(BLOCKMAP_FILENAME)
        output.putNextEntry(entry)
        output.write(bytes)
        output.closeEntry()
      }
      return buffer.toByteArray()
    }
  }

  private fun getFileInputStream(bucketName: String, filePath: String): InputStream {
    return s3Client.getObject { getObjectRequest ->
      getObjectRequest
        .bucket(bucketName)
        .key(filePath)
    }
  }

  private fun getNewFilePath(oldFilePath: String, newFileSuffix: String): String {
    val suffix = if(oldFilePath.endsWith(".zip")) ".zip" else ".jar"
    return oldFilePath.replace(suffix, newFileSuffix)
  }

  private fun getKeyFromPath(bucketPrefix: String, filePath: String): String {
    return if (bucketPrefix.isEmpty()) filePath else "$bucketPrefix/${filePath.removePrefix("/")}"
  }
}
