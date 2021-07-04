package com.manoharprabhu.pngtool

import com.manoharprabhu.pngtool.chunk.Chunk
import com.manoharprabhu.pngtool.chunk.IHDRChunk
import com.manoharprabhu.pngtool.exceptions.InvalidChunkDataException
import com.manoharprabhu.pngtool.exceptions.InvalidHeaderException
import com.manoharprabhu.pngtool.exceptions.MissingChunkException
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

class PNGTool(file: File) {
    private val byteBuffer: ByteBuffer
    private val chunkDictionary: HashMap<String, MutableList<Chunk>> = linkedMapOf()


    init {
        if(file.isDirectory) {
            throw IOException("Expecting a file")
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
        validateChunks()
    }

    private fun validateChunks() {
        if(!chunkDictionary.containsKey("IHDR")) {
            throw MissingChunkException("No IHDR chunk present in the image")
        }

        val ihdrChunk = chunkDictionary["IHDR"]?.get(0) as IHDRChunk

        if(ihdrChunk.colorType == 3 && !chunkDictionary.containsKey("PLTE")) {
            throw InvalidChunkDataException("Color type is 3, but no PLTE chunk present")
        }

        if((ihdrChunk.colorType == 0 || ihdrChunk.colorType == 4) && chunkDictionary.containsKey("PLTE")) {
            throw InvalidChunkDataException("PLTE chunk should not be present for color type ${ihdrChunk.colorType}")
        }

        if(chunkDictionary.containsKey("PLTE")) {
            if(chunkDictionary["PLTE"]!!.size > 1) {
                throw InvalidChunkDataException("More than one PLTE chunk found")
            }
        }

        if(getAllChunkTypes()[0] != "IHDR") {
            throw InvalidChunkDataException("IHDR must be the first chunk")
        }
    }

    private fun parseChunks() {
        while(byteBuffer.remaining() > 0) {
            val chunk: Chunk = getNextChunk()
            if(!chunkDictionary.containsKey(chunk.typeString)) {
                chunkDictionary[chunk.typeString] = mutableListOf()
            }
            chunkDictionary[chunk.typeString]?.add(chunk)
        }
    }

    private fun getNextChunk(): Chunk {
        val length = byteBuffer.int
        if(length < 0 || length >= 1024 * 1024 * 1024) { // 1GB
            throw InvalidChunkDataException("Chunk is too big. ${length.toUInt()} bytes")
        }
        val type = extractBytes(byteBuffer, 4)
        val data = ByteArray(length)
        byteBuffer.get(data, 0, length)
        val crc: Int = byteBuffer.int
        return Chunk.makeChunk(length, type, data, crc)
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
            throw InvalidHeaderException("Invalid header")
        }
    }
}