package com.example.harryerayaudiorecorder
import android.util.Log
import java.io.*
object AudioConversionUtils {
    @Throws(IOException::class)
    fun rawToWave(rawFile: File, waveFile: File) {
        val rawData = ByteArray(rawFile.length().toInt())
        var input: DataInputStream? = null
        try {
            input = DataInputStream(FileInputStream(rawFile))
            input.read(rawData)
        } finally {
            input?.close()
        }
        var output: DataOutputStream? = null
        try {
            output = DataOutputStream(FileOutputStream(waveFile))
            writeString(output, "RIFF")
            writeInt(output, 36 + rawData.size)
            writeString(output, "WAVE")
            writeString(output, "fmt ")
            writeInt(output, 16)
            writeShort(output, 1.toShort())
            writeShort(output, 1.toShort())
            writeInt(output, 44100)
            writeInt(output, 44100 * 2)
            writeShort(output, 2.toShort())
            writeShort(output, 16.toShort())
            writeString(output, "data")
            writeInt(output, rawData.size)
            output.write(fullyReadFileToBytes(rawFile))
        } finally {
            output?.close()
            Log.d("AudioConversionUtil","file written")
        }
    }

    @Throws(IOException::class)
    fun fullyReadFileToBytes(f: File): ByteArray {
        val size = f.length().toInt()
        val bytes = ByteArray(size)
        val tmpBuff = ByteArray(size)
        val fis = FileInputStream(f)
        try {
            var read = fis.read(bytes, 0, size)
            if (read < size) {
                var remain = size - read
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain)
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read)
                    remain -= read
                }
            }
        } finally {
            fis.close()
        }
        return bytes
    }

    @Throws(IOException::class)
    private fun writeInt(output: DataOutputStream, value: Int) {
        output.write(value shr 0)
        output.write(value shr 8)
        output.write(value shr 16)
        output.write(value shr 24)
    }

    @Throws(IOException::class)
    private fun writeShort(output: DataOutputStream, value: Short) {
        output.write(value.toInt() shr 0)
        output.write(value.toInt() shr 8)
    }

    @Throws(IOException::class)
    private fun writeString(output: DataOutputStream, value: String) {
        for (i in 0 until value.length) {
            output.write(value[i].code)
        }
    }
}
