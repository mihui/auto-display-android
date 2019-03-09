package com.ibm.cic.kotlin.starterkit.activities

import android.annotation.SuppressLint
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.ibm.cic.kotlin.starterkit.application.R
import com.ibm.cic.kotlin.starterkit.models.BLEModel

class DeviceActivity : BaseActivity() {

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)

        val model = intent.getSerializableExtra("model") as BLEModel
        println("### MODEL ###")
        println(model)
        println("### /MODEL ###")
        title = model.name
    }
}
