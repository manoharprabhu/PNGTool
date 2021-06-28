package com.manoharprabhu.chunk

class PLTEChunk(length: Int, type: ByteArray, data: ByteArray, crc: Int) : Chunk(length, type, data, crc) {
    init {
        if(length % 3 != 0) {
            throw Exception("PLTE length is not a multiple of 3")
        }
    }
}