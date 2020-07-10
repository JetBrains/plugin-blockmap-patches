package com.jetbrains.plugin.blockmap.protocol

import com.fasterxml.jackson.annotation.JsonTypeInfo

class PluginBlockMapDescriptorRequest {
  lateinit var bucketName: String
  lateinit var bucketPrefix: String
  lateinit var key: String
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "result")
class PluginBlockMapDescriptorResponse(
  val blockMapFilename: String
)


