import java.io.File
import java.lang.StringBuilder
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import java.util.zip.CRC32

class PNGTool(file: File) {
    private val byteBuffer: ByteBuffer
    private val chunkDictionary: HashMap<String, MutableList<Chunk>> = linkedMapOf()
    private val crc32: CRC32 = CRC32()
    var imageWidth: Int = 0
    var imageHeight: Int = 0
    var bitDepth: Int = 0
    var colorType: Int = 0
    var compressionMethod: Int = 0
    var filterMethod: Int = 0
    var interlaceMethod: Int = 0

    init {
        if(file.isDirectory) {
            throw Exception("Expecting a file")
        }
        byteBuffer = ByteBuffer.wrap(file.readBytes())
        byteBuffer.order(ByteOrder.BIG_ENDIAN)
        parsePNG()
    }

    fun getAllChunkTypes(): List<String> {
        return chunkDictionary.keys.toList()
    }

    fun getChunks(type: String): List<Chunk> {
        if(chunkDictionary.containsKey(type)) {
            return Collections.unmodifiableList(chunkDictionary[type])
        }
        return listOf()
    }

    private fun parsePNG() {
        validateHeader()
        parseChunks()
        confirmChunkPresence()
        parseIHDR()
    }

    private fun parseIHDR() {
        val byteBuffer = ByteBuffer.wrap(chunkDictionary["IHDR"]?.get(0)?.data)
        imageWidth = byteBuffer.int
        imageHeight = byteBuffer.int
        bitDepth = byteBuffer.get().toInt()
        colorType = byteBuffer.get().toInt()
        compressionMethod = byteBuffer.get().toInt()
        filterMethod = byteBuffer.get().toInt()
        interlaceMethod = byteBuffer.get().toInt()
    }

    private fun confirmChunkPresence() {
        if(!chunkDictionary.containsKey("IHDR")) {
            throw Exception("No IHDR chunk present in the image")
        }
    }

    private fun parseChunks() {
        while(byteBuffer.remaining() > 0) {
            val chunk: Chunk = getNextChunk()
            validateCRC(chunk)
            if(!chunkDictionary.containsKey(chunk.typeString)) {
                chunkDictionary[chunk.typeString] = mutableListOf()
            }
            chunkDictionary[chunk.typeString]?.add(chunk)
        }
    }

    private fun validateCRC(chunk: Chunk) {
        crc32.reset()
        crc32.update(chunk.type)
        crc32.update(chunk.data)
        if(crc32.value.toInt() != chunk.crc) {
            throw Exception("CRC validation failed for chunk $chunk")
        }
    }

    private fun getNextChunk(): Chunk {
        val length = byteBuffer.int
        val type = extractBytes(byteBuffer, 4)
        val data = ByteArray(length)
        byteBuffer.get(data, 0, length)
        val crc: Int = byteBuffer.int
        return Chunk(length, type, data, crc)
    }

    private fun extractBytes(byteBuffer: ByteBuffer, length: Int): ByteArray {
        val buffer = ByteArray(length)
        for(i in 0 until length) {
            buffer[i] = byteBuffer.get()
        }
        return buffer
    }

    private fun validateHeader() {
        if(byteBuffer.int != -1991225785 || byteBuffer.int != 218765834) {
            throw Exception("Invalid header")
        }
    }
}