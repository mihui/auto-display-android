package com.ibm.cic.kotlin.starterkit.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ibm.cic.kotlin.starterkit.application.OneApplication

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        val toolbar = supportActionBar
        toolbar?.setHomeButtonEnabled(true)
        toolbar?.setDisplayHomeAsUpEnabled(true)
        toolbar?.setDefaultDisplayHomeAsUpEnabled(true)
    }

}