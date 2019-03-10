package com.ibm.cic.kotlin.starterkit.interfaces

import android.bluetooth.BluetoothDevice
import com.ibm.cic.kotlin.starterkit.models.BLEModel

interface OnBLEItemClickInterface {

    fun onClick(model: BLEModel)
}