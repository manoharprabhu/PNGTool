import java.nio.ByteBuffer

class IHDRChunk(length: Int, type: ByteArray, data: ByteArray, crc: Int) : Chunk(length, type, data, crc) {
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