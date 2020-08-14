package com.jetbrains.plugin.blockmap.protocol

class PluginBlockMapDescriptorRequest {
  lateinit var bucketName: String
  lateinit var bucketPrefix: String
  lateinit var key: String
}
