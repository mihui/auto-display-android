package com.ibm.cic.kotlin.starterkit.activities

import android.bluetooth.*
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.ibm.cic.kotlin.starterkit.application.R
import com.ibm.cic.kotlin.starterkit.constants.Fufinity
import com.ibm.cic.kotlin.starterkit.helpers.DeviceManager
import com.ibm.cic.kotlin.starterkit.interfaces.IConnectionCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.text.method.ScrollingMovementMethod
import android.widget.TextView
import com.ibm.cic.kotlin.starterkit.application.BuildConfig
import kotlinx.android.synthetic.main.activity_device.*

import java.util.UUID.fromString

class DeviceActivity : BaseActivity() {

    var loadingView: LinearLayout? = null
    var logTextView: TextView? = null
    var bleAddress: String? = null
    var currentGatt: BluetoothGatt? = null

    companion object {

        var UUID_SERVICE = fromString("0000fff0-0000-1000-8000-00805f9b34fb")!!
        var UUID_CHARACTERISTIC = fromString("00002902-0000-1000-8000-00805f9b34fb")!!
        var UUID_DESCRIPTOR = fromString("00002902-0000-1000-8000-00805f9b34fb")!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device)

        initUI()

        if(BuildConfig.DEBUG) {

            UUID_SERVICE = fromString("00001800-0000-1000-8000-00805f9b34fb")!!
            UUID_CHARACTERISTIC = fromString("00002a00-0000-1000-8000-00805f9b34fb")!!
            UUID_DESCRIPTOR = fromString("00002902-0000-1000-8000-00805f9b34fb")!!
        }
    }

    private fun broadcastNow(action: String): Boolean {

        val intent = Intent(action)
        return LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun broadcastNow(action: String, characteristic: BluetoothGattCharacteristic?): Boolean {

        val intent = Intent(action)

        if(UUID_CHARACTERISTIC == characteristic?.uuid) {

            intent.putExtra(DeviceManager.INTENT_DATA, characteristic?.value)
        }

        return LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    private fun initUI() {

        loadingView = findViewById(R.id.layout_loading)
        logTextView = findViewById(R.id.txt_log)
        logTextView?.movementMethod = ScrollingMovementMethod.getInstance()

        bleAddress = intent.getStringExtra("address")
        val name  = intent.getStringExtra("name")

//        println("### ADDRESS ###")
//        println(bleAddress)
//        println(name)
//        println("### /ADDRESS ###")

        title = name
        loadingView?.visibility = View.VISIBLE

        DeviceManager.connect(bleAddress, object: IConnectionCallback<BluetoothGatt?> {

            override fun onConnected(payload: BluetoothGatt?) {

                appendLog("### CONNECTED WITH: $bleAddress")

                runOnUiThread {

                    loadingView?.visibility = View.GONE
                }

                currentGatt = payload
                currentGatt?.discoverServices()

                btn_disconnect.isEnabled = true

                showMessage(getString(R.string.connected))
                broadcastNow(DeviceManager.DEVICE_CONNECTED)
            }

            override fun onDisconnected(payload: BluetoothGatt?) {

                appendLog("### DISCONNECTED ###")

                broadcastNow(DeviceManager.DEVICE_DISCONNECTED)

                finish()
            }

            override fun onDiscoveredServices(gatt: BluetoothGatt?, list: List<BluetoothGattService>?) {

                appendLog("### DeviceActivity.onDiscoveredServices ###")

                currentGatt = gatt

                list?.forEach { service ->

                    appendLog("### ${service.uuid} ###")

                    appendLog("### SERVICE: ${service.uuid}")

                    service.characteristics.forEach { char ->

                        appendLog("### CHARACTERISTIC: ${char.uuid}")

                        when(char.properties) {

                            BluetoothGattCharacteristic.PROPERTY_READ -> {

                                currentGatt?.readCharacteristic(char)
                                appendLog("### READABLE: ${service.uuid}:${char.uuid} ###")
                            }

                            BluetoothGattCharacteristic.PROPERTY_WRITE -> {

                                appendLog("### WRITABLE: ${service.uuid}:${char.uuid} ###")
                            }

                            BluetoothGattCharacteristic.PROPERTY_BROADCAST -> {

                                currentGatt?.readCharacteristic(char)
                                appendLog("### BROADCAST: ${service.uuid}:${char.uuid} ###")
                            }

                            BluetoothGattCharacteristic.PROPERTY_NOTIFY -> {

                                currentGatt?.setCharacteristicNotification(char, true)
                                appendLog("### NOTIFY: ${service.uuid}:${char.uuid} ###")
                            }

                            BluetoothGattCharacteristic.PROPERTY_INDICATE -> {

                                currentGatt?.setCharacteristicNotification(char, true)
                                appendLog("### INDICATE: ${service.uuid}:${char.uuid} ###")
                            }

                            BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE -> {

                                appendLog("### SIGNED_WRITE: ${service.uuid}:${char.uuid} ###")
                            }

                            BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE -> {

                                appendLog("### WRITE_NO_RESPONSE: ${service.uuid}:${char.uuid} ###")
                            }
                        }
                    }

                    broadcastNow(DeviceManager.DEVICE_DISCOVERED)
                    when(service.uuid) {

                        UUID_SERVICE -> enableNotification() //broadcastNow(DeviceManager.DEVICE_DISCOVERED) // change to broadcast

//                        Fufinity.SERVICE_TEST_A -> {
//
//                            appendLog("### SERVICE_TEST_A ###")
//                        }
//
//                        Fufinity.SERVICE_TEST_B -> {
//
//                            appendLog("### SERVICE_TEST_B ###")
//                        }
//
//                        Fufinity.SERVICE_TEST_C -> {
//
//                            appendLog("### SERVICE_TEST_C ###")
//                        }
//
//                        Fufinity.SERVICE_TEST_D -> {
//
//                            appendLog("### SERVICE_TEST_D ###")
//                        }
                    }
                }
            }

            override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {

//                val d3 = convert(characteristic?.value!!)

//                println("### READ: ${d3.z}/${d3.y}/${d3.x} °C ###")

//                val stringResult = characteristic?.getStringValue(0)
//                val bytesResult = characteristic?.value
//                val intResult = characteristic?.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0)

                appendLog("### DESCRIPTOR SIZE: ${characteristic?.descriptors?.size}")

//                println("### READ[${characteristic?.service?.uuid}]: $stringResult ###")
//                println("### READ[${characteristic?.service?.uuid}]: $bytesResult ###")
//                println("### READ[${characteristic?.service?.uuid}]: $intResult ###")
                broadcastNow(DeviceManager.DEVICE_DATA_AVAILABLE)
            }

            override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {

//                val stringResult = characteristic?.getStringValue(0)
//                val bytesResult = characteristic?.value
//                val intResult = characteristic?.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0)
//                println("### WRITE[${characteristic?.service?.uuid}]: $stringResult ###")
//                println("### WRITE[${characteristic?.service?.uuid}]: $bytesResult ###")
//                println("### WRITE[${characteristic?.service?.uuid}]: $intResult ###")

//                characteristic?.descriptors?.forEach { x ->
//
//                    run {
//                        appendLog("### DESCRIPTOR: ${x.uuid}")
//                    }
//                }
                appendLog("### DESCRIPTOR SIZE: ${characteristic?.descriptors?.size}")
            }

            override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {

                broadcastNow(DeviceManager.DEVICE_DATA_AVAILABLE, characteristic)
            }
        })

        val disconnectButton = findViewById<Button>(R.id.btn_disconnect)
        disconnectButton.setOnClickListener {

            disConnect(bleAddress)
        }
    }

    fun enableNotification() {

        appendLog("### ENABLING NOTIFICATION ###")
        // SPECIFIC A UUID
        val gattService = currentGatt?.getService(UUID_SERVICE)
        if(gattService == null) {

            showMessage("${getString(R.string.service_not_found)}: $UUID_SERVICE")
            broadcastNow(DeviceManager.DEVICE_NOT_SUPPORTED)
            return
        }

        val gattCharacteristic = gattService.getCharacteristic(UUID_CHARACTERISTIC)
        if(gattCharacteristic == null) {

            showMessage(getString(R.string.characteristic_not_found))
            broadcastNow(DeviceManager.DEVICE_NOT_SUPPORTED)
            return
        }

        val gattDescriptor = gattCharacteristic.getDescriptor(UUID_DESCRIPTOR)
        if(gattDescriptor == null) {

            showMessage(getString(R.string.descriptor_not_found))
            broadcastNow(DeviceManager.DEVICE_NOT_SUPPORTED)
            return
        }
        gattDescriptor.value = ENABLE_NOTIFICATION_VALUE
        currentGatt?.writeDescriptor(gattDescriptor)
        /// SPECIFIC A UUID
    }

    private fun disConnect(address: String?) {

        println(String.format("### Disconnecting device: %s ###", DeviceManager.getState(bleAddress)))

        DeviceManager.disconnect(address)

        btn_disconnect.isEnabled = false
        println(String.format("### Disconnected device: %s ###", DeviceManager.getState(bleAddress)))
    }

    fun showMessage(message: String) {

        runOnUiThread {

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            appendLog(message)
        }
    }

    private fun appendLog(message: String?) {

        runOnUiThread {
            logTextView?.append(message ?: "---")
            logTextView?.append("\n")
        }
        println(message)
    }

    inner class Point3D constructor(_x: Double, _y: Double, _z: Double){
        var x: Double = _x
        var y: Double = _y
        var z: Double = _z
    }

    fun convert(value: ByteArray): Point3D {

        val ambient = extractAmbientTemperature(value)
        val target = extractTargetTemperature(value, ambient)
        val targetNewSensor = extractTargetTemperatureTMP007(value)
        return Point3D(ambient, target, targetNewSensor)
    }

    private fun extractAmbientTemperature(v: ByteArray): Double {
        val offset = 2
        return shortUnsignedAtOffset(v, offset) / 128.0
    }

    private fun extractTargetTemperature(v: ByteArray, ambient: Double): Double {

        val twoByteValue = shortSignedAtOffset(v, 0)

        var vObj2 = twoByteValue.toDouble()
        vObj2 *= 0.00000015625

        val Tdie = ambient + 273.15

        val S0 = 5.593E-14 // Calibration factor
        val a1 = 1.75E-3
        val a2 = -1.678E-5
        val b0 = -2.94E-5
        val b1 = -5.7E-7
        val b2 = 4.63E-9
        val c2 = 13.4
        val tRef = 298.15
        val sS = S0 * (1 + a1 * (Tdie - tRef) + a2 * Math.pow(Tdie - tRef, 2.0))
        val vOS = b0 + b1 * (Tdie - tRef) + b2 * Math.pow(Tdie - tRef, 2.0)
        val fObj = vObj2 - vOS + c2 * Math.pow(vObj2 - vOS, 2.0)
        val tObj = Math.pow(Math.pow(Tdie, 4.0) + fObj / sS, 0.25)

        return tObj - 273.15
    }

    private fun extractTargetTemperatureTMP007(v: ByteArray): Double {

        val offset = 0
        return shortUnsignedAtOffset(v, offset) / 128.0
    }

    private fun shortUnsignedAtOffset(c: ByteArray, offset: Int): Int {

        val lowerByte = c[offset].toInt() and 0xFF
        val upperByte = c[offset + 1].toInt() and 0xFF
        return (upperByte shl 8) + lowerByte
    }

    /**
     * Gyroscope, Magnetometer, Barometer, IR temperature all store 16 bit two's complement values as LSB MSB, which cannot be directly parsed
     * as getIntValue(FORMAT_SINT16, offset) because the bytes are stored as little-endian.
     *
     * This function extracts these 16 bit two's complement values.
     */
    private fun shortSignedAtOffset(c: ByteArray, offset: Int): Int {

        val lowerByte = c[offset].toInt() and 0xFF
        val upperByte = c[offset + 1].toInt() // // Interpret MSB as signed
        return (upperByte shl 8) + lowerByte
    }

    override fun onDestroy() {

        super.onDestroy()
    }
}
