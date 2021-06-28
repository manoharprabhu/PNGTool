package com.manoharprabhu.chunk

class PLTEChunk(length: Int, type: ByteArray, data: ByteArray, crc: Int) : Chunk(length, type, data, crc)