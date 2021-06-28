package com.manoharprabhu.chunk

import java.lang.StringBuilder
import java.util.zip.CRC32

open class Chunk(val length: Int, val type: ByteArray, val data: ByteArray, val crc: Int) {
    val typeString: String

    companion object {
        val crc32: CRC32 = CRC32()
        private val hdrBytes = byteArrayOf(0x49, 0x48, 0x44, 0x52)
        private val plteBytes = byteArrayOf(0x50, 0x4C, 0x54, 0x45)

        fun makeChunk(length: Int, type: ByteArray, data: ByteArray, crc: Int): Chunk {
            return when {
                hdrBytes.contentEquals(type) -> {
                    IHDRChunk(length, type, data, crc)
                }
                plteBytes.contentEquals(type) -> {
                    PLTEChunk(length, type, data, crc)
                }
                else -> {
                    Chunk(length, type, data, crc)
                }
            }
        }
    }

    init {
        val builder = StringBuilder()
        for(i in 0..3) {
            builder.append(type[i].toInt().toChar())
        }
        typeString = builder.toString()
        validateCRC()
    }

    private fun validateCRC() {
        crc32.reset()
        crc32.update(type)
        crc32.update(data)
        if(crc32.value.toInt() != crc) {
            throw Exception("CRC validation failed for chunk $this")
        }
    }

    private fun isCriticalChunk(): Boolean {
        return typeString.isNotEmpty() && typeString[0] >= 'A' && typeString[0] <= 'Z'
    }
    override fun toString(): String {
        return "$typeString - $length bytes | CRC - $crc | critical? - ${isCriticalChunk()}"
    }
}