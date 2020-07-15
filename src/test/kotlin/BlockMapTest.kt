import com.jetbrains.plugin.blockmap.BlockMap
import com.jetbrains.plugin.blockmap.FastCDC
import org.junit.Test
import java.io.*
import kotlin.test.assertEquals

class BlockMapTest {

  @Test
  fun `check color image blockmap`() {
    val blockMap = createBlockMapFromFile("src/test/resources/bird.png")
    val offsets = arrayOf(0, 10793, 33390, 64095, 99926, 102438, 113119)
    val lengths = arrayOf(10793, 22597, 30705, 35831, 2512, 10681, 6164)
    validateChunks(blockMap.chunks, offsets, lengths)
  }

  @Test
  fun `check black and white image blockmap`() {
    val blockMap = createBlockMapFromFile("src/test/resources/tiger.jpg")
    val offsets = arrayOf(0, 10456, 20602, 51151, 62934, 65755)
    val lengths = arrayOf(10456, 10146, 30549, 11783, 2821, 3344)
    validateChunks(blockMap.chunks, offsets, lengths)
  }

  @Test
  fun `check blockmap serialization`() {
    val blockMap = createBlockMapFromFile("src/test/resources/three_images.zip")
    saveBlockMapToFile("src/test/resources/blockmap_three_images.bin", blockMap)
    val downloadedBlockMap = loadBlockMapFromFile("src/test/resources/blockmap_three_images.bin")
    assertEquals(downloadedBlockMap.compare(blockMap).size, 0)
  }

  @Test
  fun `check blockmaps serialization and compare`() {
    val blockMap1 = createBlockMapFromFile("src/test/resources/three_images.zip")
    val blockMap2 = createBlockMapFromFile("src/test/resources/two_images.zip")
    saveBlockMapToFile("src/test/resources/blockmap_three_images.bin", blockMap1)
    saveBlockMapToFile("src/test/resources/blockmap_two_images.bin", blockMap2)
    val downloadedBlockMap1 = loadBlockMapFromFile("src/test/resources/blockmap_three_images.bin")
    val downloadedBlockMap2 = loadBlockMapFromFile("src/test/resources/blockmap_two_images.bin")
    val result = downloadedBlockMap2.compare(downloadedBlockMap1).stream().mapToInt() { e -> e.length }.sum()
    assertEquals(result, 94726)
  }

  @Test
  fun `check compare different blockmaps`() {
    val blockMap1 = createBlockMapFromFile("src/test/resources/three_images.zip")
    val blockMap2 = createBlockMapFromFile("src/test/resources/two_images.zip")
    val result = blockMap2.compare(blockMap1).stream().mapToInt() { e -> e.length }.sum()
    assertEquals(result, 94726)
  }

  private fun validateChunks(chunks: List<FastCDC.Chunk>, offsets: Array<Int>, lengths: Array<Int>) {
    assertEquals(chunks.size, offsets.size)
    assertEquals(chunks.size, lengths.size)
    for (i in chunks.indices) {
      assertEquals(chunks[i].offset, offsets[i])
      assertEquals(chunks[i].length, lengths[i])
    }
  }

  private fun createBlockMapFromFile(file: String): BlockMap {
    val input = FileInputStream(file)
    val blockMap = BlockMap(input)
    input.close()
    return blockMap
  }

  private fun saveBlockMapToFile(file: String, blockMap: BlockMap) {
    ObjectOutputStream(FileOutputStream(file)).use { out -> out.writeObject(blockMap) }
  }

  private fun loadBlockMapFromFile(file: String): BlockMap {
    val input = ObjectInputStream(FileInputStream(file))
    val result = input.readObject() as BlockMap
    input.close()
    return result
  }
}
