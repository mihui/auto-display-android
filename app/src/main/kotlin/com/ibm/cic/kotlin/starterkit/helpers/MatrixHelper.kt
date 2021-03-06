package com.ibm.cic.kotlin.starterkit.helpers

import android.content.Context
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList

class MatrixHelper(private val context: Context) {

    private val all_16_32 = 16
    private val all_2_4 = 2
    private val all_32_128 = 32

    /**
     * Hex string to Byte Array
     * @param hexString the hex string
     * *
     * @return ByteArray
     */
    private fun hexStringToBytes(v: String): ByteArray {

        var hexString = v
        hexString = hexString.toUpperCase()
        val length = hexString.length / 2
        val hexChars = hexString.toCharArray()
        val d = ByteArray(length)
        for (i in 0 until length) {
            val pos = i * 2
            d[i] = (charToByte(hexChars[pos]).toInt() shl 4 or charToByte(hexChars[pos + 1]).toInt()).toByte()
        }
        return d
    }
    /**
     * Convert char to byte
     * @param c char
     * *
     * @return byte
     */
    private fun charToByte(c: Char): Byte {

        return "0123456789ABCDEF".indexOf(c).toByte()
    }

    private fun toByteArray(x: Char, y: Char): ByteArray {

        return byteArrayOf('0'.toByte(), 'x'.toByte(), x.toUpperCase().toByte(), y.toUpperCase().toByte())
    }

    fun getMatrixList(str: String, index: Int): ByteArray {

        var data: ByteArray? = null
        var code: IntArray? = null
        var byteCount: Int
        var list: ByteArray
        var headers = byteArrayOf(0xFF.toByte(), 0xEE.toByte(), 0x01.toByte(), 0x00.toByte(), str.length.toByte())

        for (i in str.indices) {

            if (str[i].toInt() < 0x80) {
                continue
            }
            code = getByteCode(str.substring(i, i + 1))
            data = read(code[0], code[1])
            if (data == null) { break }
            byteCount = 0
            var lineCount = 0

            for (line in 0 until all_16_32) {
                for (k in 0 until all_2_4) {
                    var binaryString = ""
                    for (j in 0..7) {

                        if (data[byteCount].toInt() shr (7 - j) and 0x1 == 1) {
//                            print("❄")
                            binaryString += "1"
                        }
                        else {
//                            print(" ")
                            binaryString += "0"
                        }
                    }
                    byteCount++

                    val decimalValue = Integer.valueOf(binaryString, 2)
//                    val hexValue = Integer.toHexString(decimalValue)
                    val hexValue = "%02X".format(decimalValue)

                    val byte = hexStringToBytes(hexValue)
                    println(hexValue+": "+byte.size)
                    headers += (byte)
//                    val byteArray = byteArrayOf('0'.toByte(), 'x'.toByte(), hexValue[0].toUpperCase().toByte(), hexValue[1].toUpperCase().toByte())

                    //list.add(byteArray)
//                    println("$lineCount: $hexValue")
                    lineCount++
                }
//                println()
            }
        }

        return headers
    }

    private fun read(areaCode: Int, posCode: Int): ByteArray? {

        var data: ByteArray? = null
        val assManager = context.assets

        try {
            val area = areaCode - 0xa0
            val pos = posCode - 0xa0
            val ins: InputStream = assManager.open("fonts/hzk16y")//FileInputStream(File(fontPath)) //Files.newInputStream(Paths.get(fontPath), StandardOpenOption.READ)

            val offset = (all_32_128 * ((area - 1) * 94 + pos - 1)).toLong()
            ins.skip(offset)
            data = ByteArray(all_32_128)
            ins.read(data, 0, all_32_128)
            ins.close()

        } catch (ex: Exception) {

            System.err.println("SORRY,THE FILE CAN'T BE READ")
        }

        return data
    }

    private fun getByteCode(str: String): IntArray {

        val byteCode = IntArray(2)
        try {

            val data = str.toByteArray(charset("GB2312"))
            byteCode[0] = if (data[0] < 0) 256 + data[0] else 0 + data[0]
            byteCode[1] = if (data[1] < 0) 256 + data[1] else 0 + data[1]

        } catch (ex: Exception) {

            ex.printStackTrace()
        }

        return byteCode
    }

}