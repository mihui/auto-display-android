package com.ibm.cic.kotlin.starterkit.models

import android.bluetooth.BluetoothDevice
import android.content.res.Resources
import com.ibm.cic.kotlin.starterkit.application.R
import java.io.Serializable

class BLEModel constructor(bleName: String, bleAddress: String, bleDevice: BluetoothDevice?, _rssi: Int) : Serializable {

    var device: BluetoothDevice? = bleDevice
    var rssi: Int = _rssi
    var address: String = bleAddress
        get() {
            if(device == null || device?.address == null) {
                return field
            }
            return device!!.address
        }

    var name: String = bleName
        get() {
            if(device == null || device?.name == null) {
                return field
            }
            return device!!.name
        }
}