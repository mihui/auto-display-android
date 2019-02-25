package com.ibm.cic.kotlin.starterkit.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ibm.cic.kotlin.starterkit.application.R

class HomeFragment : Fragment() {

    var deviceList: ArrayList<BluetoothDevice>? = null

    private lateinit var mActivity: FragmentActivity
    private lateinit var mRecyclerView: RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        println("### HomeFragment.onCreateView ###")

        initBluetooth();

        if(activity == null) {
            println("### NULL ACTIVITY")
        }
        else {
            println("### NON-NULL ACTIVITY")
            mActivity = activity as FragmentActivity
            mRecyclerView = mActivity.findViewById(R.id.recycler_bounded_devices)
//            mRecyclerView.adapter =
        }

        return inflater.inflate(R.layout.fragment_home, null)
    }

    private var mScanFilters: List<ScanFilter>? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mScanner: BluetoothLeScanner? = null
    private var mScanSettings: ScanSettings? = null
    private var mHandler: Handler? = null
    private val SCAN_PERIOD: Long = 2000
    private val REQUEST_CODE_ACCESS_FINE_LOCATION: Int = 1

    private fun initBluetooth() {

        mHandler = Handler()

        initPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun initPermission(access: String) {

        println("### INIT PERMISSION ###")
        val permission = ContextCompat.checkSelfPermission(mActivity.applicationContext, access)

        if (permission == PackageManager.PERMISSION_GRANTED) {
            initBLE()
        }
        else {
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

    private fun initBLE() {

        println("### INIT BLE ###")
        if(mActivity.packageManager!!.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {

            val bluetoothManager: BluetoothManager = activity?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            mBluetoothAdapter = bluetoothManager.adapter
            deviceList = ArrayList()

            if(mBluetoothAdapter == null || mBluetoothAdapter?.isEnabled == false) {
                val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivity(enableIntent)
            }
            else {
                mScanner = mBluetoothAdapter?.bluetoothLeScanner
                mScanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()
                mScanFilters = ArrayList()
                scanDevices(true)
            }
        }
        else {
            println("### NO BLE SUPPORT ###")
            Toast.makeText(mActivity, "The BLE is not supported!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        println("### ON REQUEST PERMISSION RESULT ###")
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
                    initBLE()
                }
                else {
                    Toast.makeText(mActivity, "The Location access has been denied!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun scanDevices(enable: Boolean) {

        println("### SCAN DEVICES ###")
        if(enable) {
            mHandler?.postDelayed({
                mScanner?.stopScan(mScanCallback)
            }, SCAN_PERIOD)
            mScanner?.startScan(mScanFilters, mScanSettings, mScanCallback)
        }
        else {
            mScanner?.stopScan(mScanCallback)
        }
    }

    private val mScanCallback = object:  ScanCallback() {

        override fun onScanResult(callbackType: Int, result: ScanResult?) {

            val device = result?.device
            if(device != null) {
                print("### SCAN CALLBACK ###")
                print(device.name)
                print(device.address)
                print(device.bondState)
                print(result.rssi)
                print("### /SCAN CALLBACK ###")
                deviceList?.add(device)
            }

        }

        override fun onScanFailed(errorCode: Int) {

            print("### ERROR ###")
            print(errorCode)
            print("### /ERROR ###")
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>?) {

            println("### ON BATCH SCAN RESULTS ###")
        }
    }


}