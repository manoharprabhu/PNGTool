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

    private fun getFile(name: String): File {
        return File(classLoader.getResource(name).path)
    }

    @Test
    fun `Test chunk presence sanity`() {
        val testFile = getFile("simple.png")
        val png = PNGTool(testFile)
        val types = png.getAllChunkTypes()
        assertEquals(listOf("IHDR", "IDAT", "IEND"), types)

        assertEquals("IHDR - 13 bytes | CRC - -1871227938 | critical? - true | width - 1 | height - 1 | bitDepth - 8 | colorType - 2 | compressionMethod - 0 | filterMethod - 0 | interlaceMethod - 0", png.getChunks("IHDR")[0].toString())
        assertEquals("IDAT - 12 bytes | CRC - 417172912 | critical? - true", png.getChunks("IDAT")[0].toString())
        assertEquals("IEND - 0 bytes | CRC - -1371381630 | critical? - true", png.getChunks("IEND")[0].toString())
        
        assertTrue(png.getChunks("IHDR").isNotEmpty())

        assertTrue(png.getChunks("NONEXIST").isEmpty())
    }

    @Test
    fun `Parse IHDR chunk sanity check`() {
        val testFile = getFile("simple.png")
        val png = PNGTool(testFile)
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
        val testFile = getFile("simple_datatamper.png")
        assertThrows(InvalidChunkCRC::class.java) {
            PNGTool(testFile)
        }
    }

    @Test
    fun `Invalid image with no IHDR`() {
        val testFile = getFile("ihdrmissing.png")
        assertThrows(MissingChunkException::class.java) {
            PNGTool(testFile)
        }
    }

    @Test
    fun `Invalid directory param`() {
        val testFolder = getFile(".")
        assertThrows(IOException::class.java) {
            PNGTool(testFolder)
        }
    }

    @Test
    fun `Invalid PNG header`() {
        val testFile = getFile("invalid_header.png")
        assertThrows(InvalidHeaderException::class.java) {
            PNGTool(testFile)
        }
    }

    @Test
    fun `Valid image with a PLTE chunk`() {
        val testFile = getFile("pointer_wait_28.png")
        val png = PNGTool(testFile)
        assertEquals("PLTE - 333 bytes | CRC - -1835009928 | critical? - true | paletteEntries - 37 entries", png.getChunks("PLTE")[0].toString())
    }

    @Test
    fun `Invalid image with two PLTE chunks`() {
        val testFile = getFile("two_plte.png")
        assertThrows(InvalidChunkDataException::class.java) {
            PNGTool(testFile)
        }
    }

    @Test
    fun `Invalid image with color type 0 and a PLTE chunk`() {
        val testFile = getFile("invalid_plte_type0.png")
        assertThrows(InvalidChunkDataException::class.java) {
            PNGTool(testFile)
        }
    }

    @Test
    fun `Invalid image with color type 3 and no PLTE chunk`() {
        val testFile = getFile("no_plte_invalid.png")
        assertThrows(InvalidChunkDataException::class.java) {
            PNGTool(testFile)
        }
    }

    @Test
    fun `Invalid image with non multiple of 3 PLTE chunk`() {
        val testFile = getFile("invalid_plte_nonthree.png")
        assertThrows(InvalidChunkDataException::class.java) {
            PNGTool(testFile)
        }
    }

    @Test
    fun `Invalid color type in IHDR chunk`() {
        val testFile = getFile("invalid_ihdr_colortype.png")
        assertThrows(InvalidChunkDataException::class.java) {
            PNGTool(testFile)
        }
    }

    @Test
    fun `Invalid bit depth in IHDR chunk`() {
        val testFile = getFile("invalid_ihdr_bitdepth.png")
        assertThrows(InvalidChunkDataException::class.java) {
            PNGTool(testFile)
        }
    }

    @Test
    fun `Invalid compression mode in IHDR`() {
        val testFile = getFile("invalid_ihdr_compression.png")
        assertThrows(InvalidChunkDataException::class.java) {
            PNGTool(testFile)
        }
    }
}