package com.manoharprabhu.pngtool.chunk

import com.manoharprabhu.pngtool.exceptions.InvalidChunkDataException

class PLTEChunk(length: Int, type: ByteArray, data: ByteArray, crc: Int) : Chunk(length, type, data, crc) {
    val paletteEntries: List<PaletteEntry>
    init {
        if(length % 3 != 0) {
            throw InvalidChunkDataException("PLTE length is not a multiple of 3")
        }
        paletteEntries = parsePaletteEntries()
    }

    override fun toString(): String {
        return "PLTE - ${data.size / 3} palette entries"
    }

    private fun parsePaletteEntries(): List<PaletteEntry> {
        val list = mutableListOf<PaletteEntry>()
        for(i in data.indices step 3) {
            list.add(PaletteEntry(data[i], data[i + 1], data[i + 2]))
        }
        return list
    }
}