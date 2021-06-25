class Chunk(val length: Int, val type: String, val data: ByteArray, val crc: Int) {
    private fun isCriticalChunk(): Boolean {
        return type.isNotEmpty() && type[0] >= 'A' && type[0] <= 'Z'
    }
    override fun toString(): String {
        return "$type - $length bytes | CRC - $crc | critical? - ${isCriticalChunk()}"
    }
}