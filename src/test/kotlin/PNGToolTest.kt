import org.junit.Test

import org.junit.Assert.*
import java.io.File
import kotlin.test.assertContains

class PNGToolTest {

    @Test
    fun parsePNG() {
        val classLoader = javaClass.classLoader
        val testFile = classLoader.getResource("simple.png").path
        val png = PNGTool(File(testFile))

        val types = png.getAllChunkTypes()
        assertEquals(listOf("IHDR", "IDAT", "IEND"), types)
    }
}