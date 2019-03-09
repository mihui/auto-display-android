package com.ibm.cic.kotlin.starterkit.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.ibm.cic.kotlin.starterkit.activities.DeviceActivity
import com.ibm.cic.kotlin.starterkit.activities.DiscoverActivity
import com.ibm.cic.kotlin.starterkit.models.BLEModel
import com.ibm.cic.kotlin.starterkit.adapters.BLEAdapter
import com.ibm.cic.kotlin.starterkit.application.R
import com.ibm.cic.kotlin.starterkit.decorations.BLEDecoration
import com.ibm.cic.kotlin.starterkit.helpers.DeviceFinder
import com.ibm.cic.kotlin.starterkit.helpers.IDeviceFinder
import com.ibm.cic.kotlin.starterkit.interfaces.OnBLEItemClickInterface

class HomeFragment : Fragment() {

    private val TAG = "BLE|HomeFragment"

    private lateinit var mActivity: FragmentActivity
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mBLEAdapter: BLEAdapter
    private lateinit var mDiscoverButton: Button
    private lateinit var deviceFinder: DeviceFinder
    private lateinit var deviceCount: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        Log.i(TAG, "### HomeFragment.onCreateView ###")

        val cView = inflater.inflate(R.layout.fragment_home, null)

        if(activity == null) {
            Log.e(TAG,"### NULL ACTIVITY")
        }
        else {
            Log.i(TAG,"### NON-NULL ACTIVITY")

            initScanner(cView)
        }

        return cView
    }

    private fun initScanner(cView: View) {

        mBLEAdapter = BLEAdapter(R.layout.item_connected_ble, object : OnBLEItemClickInterface {

            override fun onClick(model: BLEModel) {

                val intent = Intent(context, DeviceActivity::class.java)
                intent.putExtra("model", model)
                startActivity(intent)
            }
        })

        mActivity = activity as FragmentActivity
        mRecyclerView = cView.findViewById(R.id.recycler_bounded_devices)
        deviceCount = cView.findViewById<TextView>(R.id.connected_device_title)
        mDiscoverButton = cView.findViewById(R.id.btn_discover)
        mDiscoverButton.setOnClickListener(View.OnClickListener {

            val intent = Intent(context, DiscoverActivity::class.java)
            startActivity(intent)
        })

        mRecyclerView.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        mRecyclerView.adapter = mBLEAdapter

        mRecyclerView.addItemDecoration(BLEDecoration(20, 10))

        deviceFinder = DeviceFinder()

        deviceFinder.getConnectedDevices(object: IDeviceFinder {

            override fun onResult(list: ArrayList<BLEModel>) {

                mActivity.runOnUiThread {

                    deviceCount.text = getString(R.string.connected_device_list, list.size.toString())
                }

                mBLEAdapter.setDevices(list)
            }

            override fun onError(errorCode: Int) {

                Log.e(TAG, "### ERROR ###")
                Log.e(TAG, errorCode.toString())
                Log.e(TAG, "### /ERROR ###")
            }
        })
    }

}