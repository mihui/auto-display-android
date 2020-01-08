package com.ibm.cio.study.study.application

import com.ibm.cic.kotlin.starterkit.application.MainActivity
import com.ibm.cic.kotlin.starterkit.helpers.MatrixHelper
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest {

    fun hexStringToBytes(hexString: String?): ByteArray? {
        var hexString = hexString
        if (hexString == null || hexString == "") {
            return null
        }
        hexString = hexString.toUpperCase()
        val length = hexString.length / 2
        val hexChars = hexString.toCharArray()
        val d = ByteArray(length)
        for (i in 0..length - 1) {
            val pos = i * 2
            d[i] = (charToByte(hexChars[pos]).toInt() shl 4 or charToByte(hexChars[pos + 1]).toInt()).toByte()
        }
        return d
    }

    private fun charToByte(c: Char): Byte { return "0123456789ABCDEF".indexOf(c).toByte() }

    @Test
    fun addition_isCorrect() {


//        val str = "一二三四五六七八九十一二三四五     ";
//        println(str.length)
//        assertEquals(4, 2 + 2)
//        val charset = Charsets.UTF_8
//        val byteArray = 0x91
//        println(byteArray)
        val binaryString = "1001";
        val decimalValue = Integer.valueOf(binaryString, 2)
        val hexValue = "%02X".format(decimalValue)
        println(hexStringToBytes(hexValue)?.size)
    }

    @Test
    fun matrixHelper() {

        val activity = Robolectric.setupActivity(MainActivity::class.java)
        val matrixHelper = MatrixHelper(activity)
        val list = matrixHelper.getMatrixList("测试字符串", 0)

//        val byteList: ArrayList<Byte> = ArrayList(list.size * 4)

        list.forEach {

        }

        println(list.size)

//        val decimalValue = Integer.valueOf(binaryString, 2)
//        println(Integer.toHexString(decimalValue));
    }
}
