package com.ibm.cic.kotlin.starterkit.helpers

import android.app.Activity

class LogHelper {

    companion object {
        var logTag: String = "########"

        fun logString(str: String, tag: String = logTag) {
            android.util.Log.e(tag, str)
        }
        fun logString(str: String, tag: Activity) {
            android.util.Log.e("[" + tag.title.toString().toUpperCase() + "]", str)
        }
    }
}