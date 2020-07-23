import com.fasterxml.jackson.databind.ObjectMapper
import com.jetbrains.plugin.blockmap.core.BlockMap
import com.jetbrains.plugin.blockmap.core.Chunk
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.test.assertEquals

class BlockMapTest {
  private val file1 = generateTestData(seed = 567812)
  private val file2 = generateTestData(seed = 1223)
  private val file3 = generateTestData(seed = 98224)
  private val testFile1 = concatenateFiles(file1, file3)
  private val testFile2 = concatenateFiles(file1, file2, file3)
  private val blockMap1 = BlockMap(testFile1.inputStream())
  private val blockMap2 = BlockMap(testFile2.inputStream())
  private val mapper = ObjectMapper()

  @Test
  fun `check blockmap chunks`() {
    val blockMap = generateTestBlockMap(seed = 123)
    val offsets = arrayOf(0, 9650, 12298, 21255, 24106, 33702, 46433, 48519, 68344, 76174, 92962, 96103)
    val lengths = arrayOf(9650, 2648, 8957, 2851, 9596, 12731, 2086, 19825, 7830, 16788, 3141, 3897)
    validateChunks(blockMap.chunks, offsets, lengths)
  }

  @Test
  fun `check blockmap serialization`() {
    val blockMap = generateTestBlockMap(seed = 12345)
    val restoredBlockMap = build(toJson(blockMap))
    assertEquals(compare(blockMap, restoredBlockMap).size, 0)
  }

  @Test
  fun `check compare different blockmaps`() {
    val result = calcChunksLength(compare(blockMap1, blockMap2))
    assertEquals(result, 103542)
  }

  @Test
  fun `check blockmaps serialization and compare`() {
    val restoredBlockMap1 = build(toJson(blockMap1))
    val restoredBlockMap2 = build(toJson(blockMap2))
    val result = calcChunksLength(compare(restoredBlockMap1, restoredBlockMap2))
    assertEquals(result, 103542)
  }

  @Test
  fun `check blockmaps correction working`() {
    val oldMap = blockMap1.chunks.associateBy { it.hash }

    ByteArrayOutputStream().use { baos ->
      for (newChunk in blockMap2.chunks) {
        val oldChunk = oldMap[newChunk.hash]
        if (oldChunk != null) {
          baos.write(testFile1, oldChunk.offset, oldChunk.length)
        } else {
          baos.write(testFile2, newChunk.offset, newChunk.length)
        }
      }
      assertEquals(baos.size(), testFile2.size)
      assertEquals(testFile2.contentEquals(baos.toByteArray()), true)
    }
  }

  private fun validateChunks(chunks: List<Chunk>, offsets: Array<Int>, lengths: Array<Int>) {
    assertEquals(chunks.size, offsets.size)
    assertEquals(chunks.size, lengths.size)
    for (i in chunks.indices) {
      assertEquals(chunks[i].offset, offsets[i])
      assertEquals(chunks[i].length, lengths[i])
    }
  }

  private fun concatenateFiles(vararg files: ByteArray): ByteArray {
    ByteArrayOutputStream().use { baos ->
      files.forEach { baos.write(it) }
      return baos.toByteArray()
    }
  }

  private fun generateTestBlockMap(size: Int = 100000, seed: Long = 35678): BlockMap {
    ByteArrayInputStream(generateTestData(size, seed)).use { input ->
      return BlockMap(input)
    }
  }

  private fun generateTestData(size: Int = 100000, seed: Long = 35678): ByteArray {
    ByteArrayOutputStream().use { output ->
      val random = Random(seed)
      for (i in 0 until size) output.write(random.nextInt())
      return output.toByteArray()
    }
  }

  fun compare(old: BlockMap, new: BlockMap): List<Chunk> {
    val oldSet = old.chunks.toHashSet()
    return new.chunks.filter { chunk -> !oldSet.contains(chunk) }.toList()
  }

  private fun calcChunksLength(chunks: List<Chunk>): Int = chunks.stream().mapToInt { e -> e.length }.sum()

  private fun build(json: String): BlockMap = mapper.readValue(json, BlockMap::class.java)
  private fun toJson(blockMap: BlockMap) = mapper.writeValueAsString(blockMap)
}
