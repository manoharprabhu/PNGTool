import com.manoharprabhu.pngtool.PNGTool
import com.manoharprabhu.pngtool.chunk.IHDRChunk
import com.manoharprabhu.pngtool.chunk.PLTEChunk
import com.manoharprabhu.pngtool.exceptions.InvalidChunkCRC
import com.manoharprabhu.pngtool.exceptions.InvalidChunkDataException
import com.manoharprabhu.pngtool.exceptions.InvalidHeaderException
import com.manoharprabhu.pngtool.exceptions.MissingChunkException
import org.junit.Test

import org.junit.Assert.*
import org.junit.BeforeClass
import java.io.File
import java.io.IOException

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
    fun `Test chunk presence sanity`() {
        val testFile = classLoader.getResource("simple.png").path
        val png = PNGTool(File(testFile))
        val types = png.getAllChunkTypes()
        assertEquals(listOf("IHDR", "IDAT", "IEND"), types)
        assertTrue(png.getChunks("IHDR").isNotEmpty())
        assertTrue(png.getChunks("NONEXIST").isEmpty())
    }

    @Test
    fun `Parse IHDR chunk sanity check`() {
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
    fun `Invalid image with wrong CRC`() {
        val testFile = classLoader.getResource("simple_datatamper.png").path
        assertThrows(InvalidChunkCRC::class.java) {
            PNGTool(File(testFile))
        }
    }

    @Test
    fun `Invalid image with no IHDR`() {
        val testFile = classLoader.getResource("ihdrmissing.png").path
        assertThrows(MissingChunkException::class.java) {
            PNGTool(File(testFile))
        }
    }

    @Test
    fun `Invalid directory param`() {
        val testFolder = classLoader.getResource(".").path
        assertThrows(IOException::class.java) {
            PNGTool(File(testFolder))
        }
    }

    @Test
    fun `Invalid PNG header`() {
        val testFile = classLoader.getResource("invalid_header.png").path
        assertThrows(InvalidHeaderException::class.java) {
            PNGTool(File(testFile))
        }
    }

    @Test
    fun `Valid image with a PLTE chunk`() {
        val testFile = classLoader.getResource("pointer_wait_28.png").path
        val png = PNGTool(File(testFile))
        assertEquals(111, (png.getChunks("PLTE")[0] as PLTEChunk).paletteEntries.size)
    }

    @Test
    fun `Invalid image with two PLTE chunks`() {
        val testFile = classLoader.getResource("two_plte.png").path
        assertThrows(InvalidChunkDataException::class.java) {
            PNGTool(File(testFile))
        }
    }

    @Test
    fun `Invalid image with color type 0 and a PLTE chunk`() {
        val testFile = classLoader.getResource("invalid_plte_type0.png").path
        assertThrows(InvalidChunkDataException::class.java) {
            PNGTool(File(testFile))
        }
    }

    @Test
    fun `Invalid image with color type 3 and no PLTE chunk`() {
        val testFile = classLoader.getResource("no_plte_invalid.png").path
        assertThrows(InvalidChunkDataException::class.java) {
            PNGTool(File(testFile))
        }
    }

    @Test
    fun `Invalid image with non multiple of 3 PLTE chunk`() {
        val testFile = classLoader.getResource("invalid_plte_nonthree.png").path
        assertThrows(InvalidChunkDataException::class.java) {
            PNGTool(File(testFile))
        }
    }
}