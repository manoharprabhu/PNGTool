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

        assertEquals(1, png.imageWidth)
        assertEquals(1, png.imageHeight)
        assertEquals(8, png.bitDepth)
        assertEquals(2, png.colorType)
        assertEquals(0, png.compressionMethod)
        assertEquals(0, png.filterMethod)
        assertEquals(0, png.interlaceMethod)
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
        val testFolder = classLoader.getResource("invalid_header.png").path
        assertThrows(Exception::class.java) {
            PNGTool(File(testFolder))
        }
    }
}