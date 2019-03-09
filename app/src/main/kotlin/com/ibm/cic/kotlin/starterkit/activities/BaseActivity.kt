package com.ibm.cic.kotlin.starterkit.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import com.ibm.cic.kotlin.starterkit.application.R

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