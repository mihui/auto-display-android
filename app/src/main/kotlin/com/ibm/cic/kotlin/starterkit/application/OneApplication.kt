package com.ibm.cic.kotlin.starterkit.application

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

@SuppressLint("Registered")
class OneApplication : Application() {

    companion object {

        private var oneApplication: Context? = null

        fun getInstance(): Context? {

            return oneApplication
        }
    }

    override fun onCreate() {

        super.onCreate()
        oneApplication = this

        println("*** APPLICATION CREATED ***")
    }
}