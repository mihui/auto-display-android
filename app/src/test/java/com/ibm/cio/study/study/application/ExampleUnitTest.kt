package com.ibm.cio.study.study.application

import android.content.Context
import com.ibm.cic.kotlin.starterkit.helpers.MatrixHelper
import org.junit.Test

import org.junit.Assert.*
import android.content.res.AssetManager
import com.ibm.cic.kotlin.starterkit.application.MainActivity
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Robolectric






/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
class ExampleUnitTest {

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun matrixHelper() {

        val activity = Robolectric.setupActivity(MainActivity::class.java)
        val matrixHelper = MatrixHelper(activity)
        val list = matrixHelper.getString("我爱你")

        val finalArray = ArrayList<String>()
        var byteCount = 0
        var byteIndex = 0

        list.forEach {

            if(byteCount % 2 == 0) {

                finalArray.add(it)
            }
            else {

                finalArray[byteIndex] += ", $it"
                byteIndex++
            }
            byteCount++
        }

        finalArray.forEach {

            println(it)
        }

        println(finalArray.size)

//        val decimalValue = Integer.valueOf(binaryString, 2)
//        println(Integer.toHexString(decimalValue));


    }
}
