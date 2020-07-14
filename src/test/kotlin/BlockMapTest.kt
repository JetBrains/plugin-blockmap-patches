import com.jetbrains.plugin.blockmap.BlockMap
import com.jetbrains.plugin.blockmap.FastCDC
import org.junit.Test
import java.io.FileInputStream
import kotlin.test.assertEquals

class BlockMapTest {

  @Test
  fun `check color image blockmap`(){
    val input = FileInputStream("src/test/resources/bird.png")
    val blockMap = BlockMap(input)
    input.close()
    val offsets = arrayOf(0, 10793, 33390, 64095, 99926, 102438, 113119)
    val lengths = arrayOf(10793, 22597, 30705, 35831, 2512, 10681, 6164)
    validateChunks(blockMap.chunks, offsets, lengths)
  }

  @Test
  fun `check black and white image blockmap`(){
    val input = FileInputStream("src/test/resources/tiger.jpg")
    val blockMap = BlockMap(input)
    input.close()
    val offsets = arrayOf(0, 10456, 20602, 51151, 62934, 65755)
    val lengths = arrayOf(10456, 10146, 30549, 11783, 2821, 3344)
    validateChunks(blockMap.chunks, offsets, lengths)
  }

  private fun validateChunks(chunks : ArrayList<FastCDC.Chunk>, offsets : Array<Int>, lengths : Array<Int>){
    assertEquals(chunks.size, offsets.size)
    assertEquals(chunks.size, lengths.size)
    for(i in chunks.indices){
      assertEquals(chunks[i].offset, offsets[i])
      assertEquals(chunks[i].length, lengths[i])
    }
  }
}
