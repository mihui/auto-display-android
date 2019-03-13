package com.ibm.cic.kotlin.starterkit.activities

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile.*
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import com.ibm.cic.kotlin.starterkit.application.R

class DeviceActivity : BaseActivity() {

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)

        val model = intent.getParcelableExtra("model") as BluetoothDevice?
        val name  = intent.getStringExtra("name")
        println("### MODEL ###")
        println(model)
        println(name)
        println("### /MODEL ###")
        title = name

        model?.connectGatt(this, true, object : BluetoothGattCallback() {

            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {

                println(String.format("### STATUS %s -> %s ###", status, newState))
                when(newState) {

                    STATE_CONNECTED -> {
                        showMessage("Connected")
                        gatt?.discoverServices()
                    }

                    STATE_CONNECTING -> {
                        showMessage("Connecting")
                    }

                    STATE_DISCONNECTED -> {
                        showMessage("Disconnected")
                    }

                    STATE_DISCONNECTING -> {
                        showMessage("Disconnecting")
                    }
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {

                when(status) {

                    BluetoothGatt.GATT_SUCCESS -> {
                        val list = gatt?.services
                        showMessage("Discovered "+ list?.size +" services")
                        println("### Discovered "+ list?.size + " services ###")
                    }
                    else -> {
                        println("### Discovered $status services ###")
                    }
                }
            }
        })
    }

    fun showMessage(message: String) {

        Looper.prepare()
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        Looper.loop()
    }
}
