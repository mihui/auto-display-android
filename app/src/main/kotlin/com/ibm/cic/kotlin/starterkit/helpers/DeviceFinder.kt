package com.ibm.cic.kotlin.starterkit.helpers

import android.Manifest
import android.app.AlertDialog
import android.app.Fragment
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.ibm.cic.kotlin.starterkit.application.R
import com.ibm.cic.kotlin.starterkit.models.BLEModel

interface IDeviceFinder {

    fun onResult(list: ArrayList<BLEModel>)
    fun onError(errorCode: Int)
    fun onStart()
    fun onStop()
}

open class DeviceFinder constructor(activity: FragmentActivity) : Fragment() {

    private val TAG = "BLE|DeviceFinder"

    private var mScanFilters: List<ScanFilter>? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mScanner: BluetoothLeScanner? = null
    private var mScanSettings: ScanSettings? = null
    private var mHandler: Handler? = null

    lateinit var mActivity: FragmentActivity

    private var deviceList: ArrayList<BLEModel> = ArrayList()
    private val mScanPeriod: Long = 5000

    var mDelegate: IDeviceFinder? = null

    private lateinit var mScanCallback: ScanCallback

    companion object {

        val REQUEST_CODE_ACCESS_FINE_LOCATION: Int = 1

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

                var name = "Unknown device"

                if(it.name != null) {

                    name = it.name
                }

                result.add(BLEModel(name, it.address, it, 0))
            }
            return result
        }
    }

    init {
        mActivity = activity
    }

    fun getRuntimeString(resId: Int): String {

        return mActivity.getString(resId)
    }

    fun scan(delegate: IDeviceFinder) {

        mDelegate = delegate

        mHandler = Handler()

        mScanCallback = object:  ScanCallback() {

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

        initPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun initPermission(access: String) {

        Log.i(TAG, "### INIT PERMISSION ###")

        val permission = ContextCompat.checkSelfPermission(mActivity.applicationContext, access)

        if (permission == PackageManager.PERMISSION_GRANTED) {
            checkFeature()

        }
        else {
            Log.e(TAG, "NO ACCESS")
            // Manifest.permission.ACCESS_COARSE_LOCATION
            if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity, access)) {

                val builder = AlertDialog.Builder(mActivity)
                builder.setMessage("You have previously denied the Location access, would you want to verify the access?").setPositiveButton("Yes") { _, _ ->

                    ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE_ACCESS_FINE_LOCATION)
                }.setNegativeButton("No") { _, _ ->

                    Toast.makeText(mActivity, "You still DO DOT have the access to the Location Service as you denied the permission request!", Toast.LENGTH_SHORT).show()
                }
                builder.show()
            }
            else {
                ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE_ACCESS_FINE_LOCATION)
            }
        }
    }

    fun checkFeature() {

        Log.i(TAG, "### INIT BLE ###")
        if(mActivity.packageManager!!.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {

            val bluetoothManager: BluetoothManager = mActivity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            mBluetoothAdapter = bluetoothManager.adapter
            deviceList.clear()

            if(mBluetoothAdapter == null || mBluetoothAdapter?.isEnabled == false) {
                val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                mActivity.startActivity(enableIntent)
            }
            else {
                mScanner = mBluetoothAdapter?.bluetoothLeScanner
                mScanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
                mScanFilters = ArrayList()
                scanDevices(true)
            }
        }
        else {

            Log.d(TAG,"### NO BLE SUPPORT ###")

            val list = makeDevices(20)
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

//            mHandler?.postDelayed({
//                scanDevices(false)
//            }, mScanPeriod)
        }
        else {
            mDelegate?.onStop()
            mScanner?.stopScan(mScanCallback)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        Log.i(TAG,"### ON REQUEST PERMISSION RESULT ###")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {

            REQUEST_CODE_ACCESS_FINE_LOCATION -> {

                var isGranted = false

                grantResults.forEach { it ->
                    if(it == PackageManager.PERMISSION_GRANTED) {
                        isGranted = true
                        return@forEach
                    }
                }

                if (isGranted) {
                    Toast.makeText(mActivity, "The Location access has been granted.", Toast.LENGTH_SHORT).show()
                }
                else {
                    Toast.makeText(mActivity, "The Location access has been denied!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun getConnectedDevices(delegate: IDeviceFinder) {

        mDelegate = delegate

        // TODO: Fake data
//        mDelegate?.onResult(DeviceFinder.makeDevices(3))
//        mDelegate?.onError(0)

        val manager = mActivity.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

        val list = manager.getConnectedDevices(BluetoothProfile.GATT)

        mDelegate?.onResult(DeviceFinder.makeDevices(list))

    }

    override fun onDestroy() {

        scanDevices(false)
        mDelegate = null
        super.onDestroy()
    }
}