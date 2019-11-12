package com.ibm.cic.kotlin.starterkit.interfaces

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService

interface IConnectionCallback<T> {

    fun onConnected(payload: T)
    fun onDisconnected(payload: T)
    fun onDiscoveredServices(gatt: T, list: List<BluetoothGattService>?)
    fun onCharacteristicRead(gatt: T, characteristic: BluetoothGattCharacteristic?)
    fun onCharacteristicWrite(gatt: T, characteristic: BluetoothGattCharacteristic?)
    fun onCharacteristicChanged(gatt: T?, characteristic: BluetoothGattCharacteristic?)
}