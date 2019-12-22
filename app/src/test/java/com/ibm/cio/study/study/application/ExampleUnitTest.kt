package com.ibm.cio.study.study.application

import com.ibm.cic.kotlin.starterkit.application.MainActivity
import com.ibm.cic.kotlin.starterkit.helpers.MatrixHelper
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import kotlin.experimental.and

/**
* Example local unit test, which will execute on the development machine (host).
*
* See [testing documentation](http://d.android.com/tools/testing).
*/
@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest {

@Test
fun addition_isCorrect() {


//        val str = "一二三四五六七八九十一二三四五     ";
//        println(str.length)
//        assertEquals(4, 2 + 2)
val charset = Charsets.UTF_8
val byteArray = 0x91
println(byteArray)
}

@Test
fun matrixHelper() {

val activity = Robolectric.setupActivity(MainActivity::class.java)
val matrixHelper = MatrixHelper(activity)
val list = matrixHelper.getMatrixList("测试字符串", 0)

val byteList: ArrayList<Byte> = ArrayList(list.size* 4)

list.forEach {


}

println(list.size)

//        val decimalValue = Integer.valueOf(binaryString, 2)
//        println(Integer.toHexString(decimalValue));
}
}
