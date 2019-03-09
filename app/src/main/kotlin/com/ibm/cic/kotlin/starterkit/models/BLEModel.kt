package com.ibm.cic.kotlin.starterkit.models

import android.content.res.Resources
import com.ibm.cic.kotlin.starterkit.application.R
import java.io.Serializable

class BLEModel constructor(_name: String, _address: String, _state: Int, _rssi: Int) : Serializable {

    var name: String
    var address: String
    var state: Int
    var rssi: Int

    init {
        name = _name
        address = _address
        state = _state
        rssi = _rssi
    }

}