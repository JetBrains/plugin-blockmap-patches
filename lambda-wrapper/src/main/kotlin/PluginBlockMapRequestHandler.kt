import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.jetbrains.plugin.blockmap.PluginBlockMapCreator
import com.jetbrains.plugin.blockmap.protocol.PluginBlockMapDescriptorRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.io.InputStream
import java.io.OutputStream


@Suppress("unused")
class PluginBlockMapRequestHandler : RequestStreamHandler {
  companion object {
    private val objectMapper = ObjectMapper().apply {
      registerModule(JavaTimeModule())
    }
    private val s3Client = S3Client.builder()
      .region(Region.EU_WEST_1)
      .httpClient(UrlConnectionHttpClient.builder().build())
      .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
      .build()

    private val pluginBlockMapCreator = PluginBlockMapCreator(s3Client)
  }

  private val log: Logger = LoggerFactory.getLogger(PluginBlockMapRequestHandler::class.java)

  override fun handleRequest(inputStream: InputStream, outputStream: OutputStream, context: Context) {
    val request = objectMapper.readValue(inputStream, PluginBlockMapDescriptorRequest::class.java)
    pluginBlockMapCreator.createPluginBlockMap(request)
  }
}
