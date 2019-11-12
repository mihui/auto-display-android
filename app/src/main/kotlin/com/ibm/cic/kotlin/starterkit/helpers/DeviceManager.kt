package com.ibm.cic.kotlin.starterkit.helpers

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Handler
import android.support.v4.app.FragmentActivity
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import com.ibm.cic.kotlin.starterkit.application.OneApplication
import com.ibm.cic.kotlin.starterkit.application.R
import com.ibm.cic.kotlin.starterkit.helpers.PermissionHelper.Companion.BLE_ACCESS
import com.ibm.cic.kotlin.starterkit.interfaces.IConnectionCallback
import com.ibm.cic.kotlin.starterkit.models.BLEModel
import java.lang.Exception

interface IDeviceManager {

    fun onResult(list: ArrayList<BLEModel>)
    fun onError(errorCode: Int)
    fun onStart()
    fun onStop()
}

class DeviceManager constructor(activity: FragmentActivity) : BroadcastReceiver() {

    private val TAG = "BLE|DeviceManager"

    private var localBroadcastManager: LocalBroadcastManager

    private var mScanFilters: List<ScanFilter>? = null
    private var mScanner: BluetoothLeScanner? = null
    private var mScanSettings: ScanSettings? = null
    private var mHandler: Handler? = null

    var mActivity: FragmentActivity = activity

    private var deviceList: ArrayList<BLEModel> = ArrayList()

    var mDelegate: IDeviceManager? = null
    var permissionHelper: PermissionHelper

    var currentGatt: BluetoothGatt? = null

    private var mScanCallback: ScanCallback = object:  ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult?) {

            val device = result?.device

            if(device != null) {

                //Log.i(TAG, "### SCAN CALLBACK ###")
                var name = getRuntimeString(R.string.unknown_device)

                if(device.name != null) {

                    name = device.name
                }
//                    else {
//                        return
//                    }

                //Log.d(TAG, String.format("### %s, %s, %s, %s ###", device.name, device.address, device.bondState, result.rssi))
                //Log.d(TAG, "### /SCAN CALLBACK ###")

                val model = BLEModel(name, device.address, device, result.rssi)
                var isExists = false

                deviceList.forEach {

                    if (it.address == model.address) {

                        it.name = model.name
                        it.address = model.address
                        it.rssi = model.rssi
                        isExists = true
                    }
                }

                if(isExists) {
                    return
                }
                deviceList.add( model )
            }

//            mBLEAdapter.setDevices(deviceList!!)

            mDelegate?.onResult(deviceList)

        }

        override fun onScanFailed(errorCode: Int) {

            Log.e(TAG, "### ERROR ###")
            Log.e(TAG, errorCode.toString())
            Log.e(TAG, "### /ERROR ###")
            mDelegate?.onError(errorCode)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {

            Log.i(TAG, "### ON BATCH SCAN RESULTS ###")
        }
    }

    companion object {

        const val DEVICE_CONNECTED: String = "DEVICE_CONNECTED"
        const val DEVICE_DISCONNECTED: String = "DEVICE_DISCONNECTED"
        const val DEVICE_DISCOVERED: String = "DEVICE_DISCOVERED"
        const val DEVICE_DATA_AVAILABLE: String = "DEVICE_DATA_AVAILABLE"
        const val DEVICE_NOT_SUPPORTED: String = "DEVICE_NOT_SUPPORTED"
        const val INTENT_DATA: String = "INTENT_DATA"

        const val SCAN_PERIOD: Long = 10000

        const val REQUEST_CODE_ACCESS_FINE_LOCATION: Int = 1

        private var connectedGatt: ArrayList<BluetoothGatt> = ArrayList()

        fun getBluetoothManager() : BluetoothManager {

            return OneApplication.getInstance()?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        }

        fun getBluetoothAdapter() : BluetoothAdapter {

            return BluetoothAdapter.getDefaultAdapter()
        }

        private fun getDevice(address: String?): BluetoothDevice {

            return DeviceManager.getBluetoothAdapter().getRemoteDevice(address)
        }

        fun connect(address: String?, callback: IConnectionCallback<BluetoothGatt?>) {

            if(address != null) {

                val existingGatt = find(address)

                if(existingGatt != null) {

                    callback.onConnected(existingGatt)
                    return
                }

                val gattInstance = getDevice(address).connectGatt(OneApplication.getInstance(), false, object : BluetoothGattCallback() {

                    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {

                        println(String.format("### onConnectionStateChange.STATUS: %s -> %s ###", status, newState))

                        if (status == BluetoothGatt.GATT_SUCCESS) {

                            when (newState) {

                                BluetoothProfile.STATE_CONNECTED -> {

                                    println("### CONNECTED ###")

                                    if(gatt != null) {


                                        add(gatt)
                                    }

                                    callback.onConnected(gatt)
                                }

                                BluetoothProfile.STATE_CONNECTING -> {

                                    println("### CONNECTING ###")
                                }

                                BluetoothProfile.STATE_DISCONNECTED -> {

                                    println("### DISCONNECTED ###")

                                    remove(gatt?.device?.address)

                                    callback.onDisconnected(gatt)
                                }

                                BluetoothProfile.STATE_DISCONNECTING -> {

                                    println("### DISCONNECTING ###")
                                }
                            }
                        }
                        else {
                            gatt?.close()
                        }
                    }

                    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {

                        println(String.format("### onServicesDiscovered ###"))

                        when(status) {

                            BluetoothGatt.GATT_SUCCESS -> {

                                val list = gatt?.services

                                callback.onDiscoveredServices(gatt, list)

                                println("### Discovered ${list?.size} services ###")
                            }
                            else -> {

                            }
                        }
                    }

                    override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {

                        if(status == BluetoothGatt.GATT_SUCCESS) {

                            callback.onCharacteristicRead(gatt, characteristic)
                        }
                    }

                    override fun onCharacteristicWrite(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {

                        if(status == BluetoothGatt.GATT_SUCCESS) {

                            callback.onCharacteristicWrite(gatt, characteristic)
                        }
                        else {
                            println("### FAILED ###")
                        }
                    }

                    override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {

                        callback.onCharacteristicChanged(gatt, characteristic)
                    }
                })

                val state = getState(address)

                when (state) {

                    BluetoothProfile.STATE_CONNECTED -> showMessage("Already connected")
                    BluetoothProfile.STATE_CONNECTING -> showMessage("Still connecting")
                    else -> {

                        while (state == BluetoothProfile.STATE_DISCONNECTING) {

                            println("### WAITING FOR DISCONNECTION ###")
                            Thread.sleep(1000)
                        }

                        gattInstance.connect()

                    }
                }
            }
        }

        fun getState(address: String?) : Int {

            return DeviceManager.getBluetoothManager().getConnectionState(DeviceManager.getDevice(address), BluetoothGatt.GATT)
        }

        fun makeDevices(count: Int) : ArrayList<BLEModel> {

            val result = ArrayList<BLEModel>()
            val list = ArrayList<Int>()
            for (i in 1 until count + 1) {
                list.add(i)
            }

            list.forEach {

                result.add(BLEModel(String.format("Fufinity Display - %s", it), String.format("0%s:00:00:00:00:00", it), null, it))
            }

            return result
        }

        fun makeDevices(list: List<BluetoothDevice>) : ArrayList<BLEModel> {

            val result = ArrayList<BLEModel>()
            list.forEach {

                var name = OneApplication.getInstance()?.getString(R.string.unknown_device) ?: ""

                if(it.name != null) {

                    name = it.name
                }

                result.add(BLEModel(name, it.address, it, 0))
            }
            return result
        }

        private fun showMessage(message: String) {

            Toast.makeText(OneApplication.getInstance(), message, Toast.LENGTH_SHORT).show()
        }

        fun disconnect(address: String?) {

            connectedGatt.forEach {

                if (address == it.device.address) {

                    it.disconnect()

                    return@forEach
                }
            }
        }

        fun remove(address: String?) {

            connectedGatt.forEach {

                if (address == it.device.address) {

                    println("### REMOVE ###")
                    it.close()

                    connectedGatt.remove(it)

                    return@forEach
                }
            }
        }

        fun add(gatt: BluetoothGatt?) {

            if(gatt != null) {

                connectedGatt.forEach {

                    if (gatt.device?.address == it.device.address) {

                        println("### RETURN ###")
                        return
                    }
                }

                println("### ADD ###")
                connectedGatt.add(gatt)
            }

        }

        private fun find(address: String): BluetoothGatt? {

            connectedGatt.forEach {

                if (address == it.device.address) {

                    return it
                }
            }
            return null
        }

        fun getConnectedDevices(): List<BLEModel> {

            val list = ArrayList<BluetoothDevice>()

            connectedGatt.forEach {

                list.add(it.device)
            }

            return makeDevices(list)
        }

    }

    init {

        localBroadcastManager = LocalBroadcastManager.getInstance(mActivity)
        permissionHelper = PermissionHelper(mActivity)
    }

    fun register() {

        val intentFilter = IntentFilter(BLE_ACCESS)
        mActivity.registerReceiver(this, intentFilter)
    }

    fun unRegister() {

        mActivity.unregisterReceiver(this) // registered @TODO
    }

    fun getRuntimeString(resId: Int): String {

        return mActivity.getString(resId)
    }

    fun scan(delegate: IDeviceManager) {

        mDelegate = delegate

        mHandler = Handler()

        permissionHelper.requestPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    override fun onReceive(p0: Context?, p1: Intent?) {

        println("### RECEIVED ###")
        checkFeature()
    }

    fun checkFeature() {

        Log.i(TAG, "### INIT BLE ###")
        if(mActivity.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {

            deviceList.clear()

            if(getBluetoothAdapter().isEnabled) {

                mScanner = getBluetoothAdapter().bluetoothLeScanner
                mScanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
                mScanFilters = ArrayList()
                scanDevices(true)
            }
            else {

                val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                mActivity.startActivity(enableIntent)

            }
        }
        else {

            Log.d(TAG,"### NO BLE SUPPORT ###")

            val list = makeDevices(20)
            deviceList.clear()
            deviceList.addAll(list)

            mDelegate?.onResult(deviceList)
            Toast.makeText(mActivity, "The BLE is not supported!", Toast.LENGTH_SHORT).show()
        }
    }

    fun scanDevices(enable: Boolean) {

        Log.i(TAG, "### SCAN DEVICES ###")

        if(enable) {

            mDelegate?.onStart()
            mScanner?.startScan(mScanFilters, mScanSettings, mScanCallback)

            mHandler?.postDelayed({
                scanDevices(false)
            }, SCAN_PERIOD)
        }
        else {
            mDelegate?.onStop()
            mScanner?.stopScan(mScanCallback)
        }
    }

    fun getConnectedDevices(delegate: IDeviceManager) {

        mDelegate = delegate

//        mDelegate?.onError(0)

        try {
            val list = getBluetoothManager().getConnectedDevices(BluetoothProfile.GATT)

            if(list.size == 0) {

                mDelegate?.onError(0)
            }
            else {

                mDelegate?.onResult(DeviceManager.makeDevices(list))
            }
        }
        catch (ex: Exception) {

            mDelegate?.onError(ex.hashCode())
        }
    }

}
