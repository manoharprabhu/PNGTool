import com.manoharprabhu.chunk.IHDRChunk
import com.manoharprabhu.chunk.PLTEChunk
import org.junit.Test

import org.junit.Assert.*
import org.junit.BeforeClass
import java.io.File

class PNGToolTest {

    companion object {
        private lateinit var classLoader: ClassLoader
        @BeforeClass
        @JvmStatic
        fun initialize() {
            classLoader = javaClass.classLoader
        }
    }

    @Test
    fun parsePNG() {
        val testFile = classLoader.getResource("simple.png").path
        val png = PNGTool(File(testFile))
        val types = png.getAllChunkTypes()
        assertEquals(listOf("IHDR", "IDAT", "IEND"), types)
        assertTrue(png.getChunks("IHDR").isNotEmpty())
        assertTrue(png.getChunks("NONEXIST").isEmpty())
    }

    @Test
    fun parseIHDR() {
        val testFile = classLoader.getResource("simple.png").path
        val png = PNGTool(File(testFile))
        val iHDRChunk = png.getChunks("IHDR")[0] as IHDRChunk
        assertEquals(1, iHDRChunk.imageWidth)
        assertEquals(1, iHDRChunk.imageHeight)
        assertEquals(8, iHDRChunk.bitDepth)
        assertEquals(2, iHDRChunk.colorType)
        assertEquals(0, iHDRChunk.compressionMethod)
        assertEquals(0, iHDRChunk.filterMethod)
        assertEquals(0, iHDRChunk.interlaceMethod)
    }

    @Test
    fun testCRCFail() {
        val testFile = classLoader.getResource("simple_datatamper.png").path
        assertThrows(Exception::class.java) {
            PNGTool(File(testFile))
        }
    }

    @Test
    fun testIHDRMissing() {
        val testFile = classLoader.getResource("ihdrmissing.png").path
        assertThrows(Exception::class.java) {
            PNGTool(File(testFile))
        }
    }

    @Test
    fun testPassDirectory() {
        val testFolder = classLoader.getResource(".").path
        assertThrows(Exception::class.java) {
            PNGTool(File(testFolder))
        }
    }

    @Test
    fun testInvalidHeader() {
        val testFile = classLoader.getResource("invalid_header.png").path
        assertThrows(Exception::class.java) {
            PNGTool(File(testFile))
        }
    }

    @Test
    fun testPLTEChunk() {
        val testFile = classLoader.getResource("pointer_wait_28.png").path
        val png = PNGTool(File(testFile))
        assertEquals(111, (png.getChunks("PLTE")[0] as PLTEChunk).paletteEntries.size)
    }

    @Test
    fun testTwoPLTEChunk() {
        val testFile = classLoader.getResource("two_plte.png").path
        assertThrows(Exception::class.java) {
            PNGTool(File(testFile))
        }
    }

    @Test
    fun `Invalid image with color type 0 and a PLTE chunk`() {
        val testFile = classLoader.getResource("invalid_plte_type0.png").path
        assertThrows(Exception::class.java) {
            PNGTool(File(testFile))
        }
    }
}