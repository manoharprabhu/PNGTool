import java.io.File
import java.lang.StringBuilder
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

class PNGTool(file: File) {
    private val byteBuffer: ByteBuffer
    private val chunkDictionary: HashMap<String, MutableList<Chunk>> = linkedMapOf()
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
        parseHeader()
        parseChunks()
    }

    private fun parseChunks() {
        while(byteBuffer.remaining() > 0) {
            val chunk: Chunk = getNextChunk()
            if(!chunkDictionary.containsKey(chunk.type)) {
                chunkDictionary[chunk.type] = mutableListOf()
            }
            chunkDictionary[chunk.type]?.add(chunk)
        }
    }

    private fun getNextChunk(): Chunk {
        val length = byteBuffer.int
        val type: String = extractType()
        val data = ByteArray(length)
        byteBuffer.get(data, 0, length)
        val crc: Int = byteBuffer.int
        return Chunk(length, type, data, crc)
    }

    private fun extractType(): String {
        val builder = StringBuilder()
        for(i in 0..3) {
            builder.append(byteBuffer.get().toInt().toChar())
        }
        return builder.toString()
    }

    private fun parseHeader() {
        if(byteBuffer.int != -1991225785 || byteBuffer.int != 218765834) {
            throw Exception("Invalid header")
        }
    }
}