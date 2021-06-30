package com.manoharprabhu.pngtool.chunk

import com.manoharprabhu.pngtool.exceptions.InvalidChunkDataException
import java.nio.ByteBuffer

class IHDRChunk(length: Int, type: ByteArray, data: ByteArray, crc: Int) : Chunk(length, type, data, crc) {
    companion object {
        val colorTypeBitDepthMapping: Map<Int, List<Int>> = mapOf(
            Pair(0, listOf(1, 2, 4, 8, 16)),
            Pair(2, listOf(8, 16)),
            Pair(3, listOf(1, 2, 4, 8)),
            Pair(4, listOf(8, 16)),
            Pair(6, listOf(8, 16)),
            )
    }
    var imageWidth: Int = 0
        private set
    var imageHeight: Int = 0
        private set
    var bitDepth: Int = 0
        private set
    var colorType: Int = 0
        private set
    var compressionMethod: Int = 0
        private set
    var filterMethod: Int = 0
        private set
    var interlaceMethod: Int = 0
        private set

    init {
        parseIHDR()
        validateIHDRData()
    }

    private fun validateIHDRData() {
        if(!colorTypeBitDepthMapping.containsKey(colorType)) {
            throw InvalidChunkDataException("Invalid color type in IHDR chunk = $colorType")
        }

        if(!colorTypeBitDepthMapping[colorType]!!.contains(bitDepth)) {
            throw InvalidChunkDataException("Invalid bit depth ${bitDepth} specified for color type ${colorType} in IHDR chunk. Allowed values are ${colorTypeBitDepthMapping[colorType]}")
        }

        if(compressionMethod != 0) {
            throw InvalidChunkDataException("Invalid compression method $compressionMethod. Allowed value - 0")
        }
    }

    private fun parseIHDR() {
        val byteBuffer = ByteBuffer.wrap(data)
        imageWidth = byteBuffer.int
        imageHeight = byteBuffer.int
        bitDepth = byteBuffer.get().toInt()
        colorType = byteBuffer.get().toInt()
        compressionMethod = byteBuffer.get().toInt()
        filterMethod = byteBuffer.get().toInt()
        interlaceMethod = byteBuffer.get().toInt()
    }
}