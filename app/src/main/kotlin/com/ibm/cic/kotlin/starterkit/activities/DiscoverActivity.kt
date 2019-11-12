package com.ibm.cic.kotlin.starterkit.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.ibm.cic.kotlin.starterkit.adapters.BLEAdapter
import com.ibm.cic.kotlin.starterkit.application.R
import com.ibm.cic.kotlin.starterkit.decorations.BLEDecoration
import com.ibm.cic.kotlin.starterkit.helpers.DeviceManager
import com.ibm.cic.kotlin.starterkit.helpers.IDeviceManager
import com.ibm.cic.kotlin.starterkit.interfaces.OnBLEItemClickInterface
import com.ibm.cic.kotlin.starterkit.models.BLEModel

class DiscoverActivity : BaseActivity() {

    private val TAG = "BLE|HomeFragment"

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mBLEAdapter: BLEAdapter
    private lateinit var mRefreshButton: Button
    private lateinit var deviceManager: DeviceManager
    private var isScanningStarted = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover)

        deviceManager = DeviceManager(this)
    }

    override fun onResume() {

        super.onResume()

        deviceManager.register()

        initScanner()
        scan()
    }

    override fun onDestroy() {

        deviceManager.unRegister();
        deviceManager.scanDevices(false)

        super.onDestroy()
    }

    private fun initScanner() {

        mBLEAdapter = BLEAdapter(R.layout.item_discovering_ble, object : OnBLEItemClickInterface {

            override fun onClick(model: BLEModel) {

                deviceManager.scanDevices(false)
                val intent = Intent(applicationContext, DeviceActivity::class.java)
                intent.putExtra("address", model.device?.address)
                intent.putExtra("name", model.name)
                startActivity(intent)
            }
        })

        mRecyclerView = findViewById(R.id.recycler_discover_bounded_devices)
        mRefreshButton = findViewById(R.id.btn_discover_refresh)

        mRefreshButton.setOnClickListener {

            if(isScanningStarted) {

                deviceManager.scanDevices(false)
            }
            else {
                scan()
            }
        }

        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mBLEAdapter

        mRecyclerView.addItemDecoration(BLEDecoration(0, 1))
    }

    private fun scan() {

        deviceManager.scan(object: IDeviceManager {

            override fun onStart() {
                isScanningStarted = true
                mRefreshButton.text = getString(R.string.stop)
            }

            override fun onStop() {
                isScanningStarted = false
                mRefreshButton.text = getString(R.string.refresh)
            }

            override fun onResult(list: ArrayList<BLEModel>) {

                mBLEAdapter.setDevices(list)

                runOnUiThread {
                    val deviceCount = findViewById<TextView>(R.id.discovered_device_title)
                    deviceCount.text = getString(R.string.discovered_device_list, list.size.toString())
                }
            }

            override fun onError(errorCode: Int) {

                Log.e(TAG, "### ERROR ###")
                Log.e(TAG, errorCode.toString())
                Log.e(TAG, "### /ERROR ###")
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        Log.i(TAG,"### ON REQUEST PERMISSION RESULT ###")
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {

            DeviceManager.REQUEST_CODE_ACCESS_FINE_LOCATION -> {

                var isGranted = false

                grantResults.forEach {

                    if(it == PackageManager.PERMISSION_GRANTED) {

                        isGranted = true
                        return@forEach
                    }
                }

                if (isGranted) {
                    Toast.makeText(this, "The Location access has been granted.", Toast.LENGTH_SHORT).show()
                    deviceManager.checkFeature()
                }
                else {
                    Toast.makeText(this, "The Location access has been denied!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
