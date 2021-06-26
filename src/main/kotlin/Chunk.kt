import java.lang.StringBuilder

class Chunk(val length: Int, val type: ByteArray, val data: ByteArray, val crc: Int) {
    val typeString: String

    init {
        val builder = StringBuilder()
        for(i in 0..3) {
            builder.append(type[i].toInt().toChar())
        }
        typeString = builder.toString()
    }

    private fun isCriticalChunk(): Boolean {
        return typeString.isNotEmpty() && typeString[0] >= 'A' && typeString[0] <= 'Z'
    }
    override fun toString(): String {
        return "$typeString - $length bytes | CRC - $crc | critical? - ${isCriticalChunk()}"
    }
}