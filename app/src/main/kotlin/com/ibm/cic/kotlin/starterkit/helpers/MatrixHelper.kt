package com.ibm.cic.kotlin.starterkit.helpers

import android.content.Context
import java.io.InputStream

class MatrixHelper(private val context: Context) {

    private val all_16_32 = 16
    private val all_2_4 = 2
    private val all_32_128 = 32

    fun getString(str: String): Array<String> {

        var data: ByteArray? = null
        var code: IntArray? = null
        var byteCount: Int

        val list = Array(all_32_128) { "0x00" }

        for (i in str.indices) {

            if (str[i].toInt() < 0x80) { continue }
            code = getByteCode(str.substring(i, i + 1))
            data = read(code[0], code[1])
            if(data == null) { break }

            byteCount = 0
            var lineCount = 0
            for (line in 0 until all_16_32) {

                for (k in 0 until all_2_4) {

                    var binaryString = ""
                    for (j in 0..7) {

                        if (data[byteCount].toInt() shr (7 - j) and 0x1 == 1) {
//                            print("❄")
                            binaryString += "1"
                        } else {
//                            print(" ")
                            binaryString += "0"
                        }

                    }
                    byteCount++

                    val decimalValue = Integer.valueOf(binaryString, 2)

                    val hexValue = "0x%02x".format(decimalValue)

                    println("$lineCount: $hexValue")
                    list[lineCount++] = hexValue
                }
//                println()
            }
        }
        return list
    }

    private fun read(areaCode: Int, posCode: Int): ByteArray? {

        var data: ByteArray? = null
        val assManager = context.getAssets()

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