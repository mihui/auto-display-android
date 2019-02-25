package com.ibm.cic.kotlin.starterkit

class BLEModel constructor(_name: String, _address: String, _state: Int, _rssi: Int) {

    var name: String
        get() = field
    var address: String
        get() = field
    var state: Int
        get() = field
    var rssi: Int
        get() = field

    init {
        name = _name
        address = _address
        state = _state
        rssi = _rssi
    }

}