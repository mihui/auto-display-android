package com.ibm.cic.kotlin.starterkit.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ibm.cic.kotlin.starterkit.BLEModel
import com.ibm.cic.kotlin.starterkit.adapters.BLEAdapter
import com.ibm.cic.kotlin.starterkit.application.R
import com.ibm.cic.kotlin.starterkit.helpers.DeviceFinder
import com.ibm.cic.kotlin.starterkit.helpers.DeviceFinder.Companion.REQUEST_CODE_ACCESS_FINE_LOCATION
import com.ibm.cic.kotlin.starterkit.helpers.IDeviceFinder

class HomeFragment : Fragment() {

    private lateinit var mActivity: FragmentActivity
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mBLEAdapter: BLEAdapter

    private lateinit var deviceFinder: DeviceFinder


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        println("### HomeFragment.onCreateView ###")

        val cView = inflater.inflate(R.layout.fragment_home, null);

        if(activity == null) {
            println("### NULL ACTIVITY")
        }
        else {
            println("### NON-NULL ACTIVITY")

            mActivity = activity as FragmentActivity
            mRecyclerView = cView.findViewById(R.id.recycler_bounded_devices)
            mBLEAdapter = BLEAdapter()

            mRecyclerView.layoutManager = LinearLayoutManager(mActivity)
            mRecyclerView.adapter = mBLEAdapter

            deviceFinder = DeviceFinder()

            deviceFinder.scan(mActivity, Finder())
        }

        return cView
    }

    inner class Finder : IDeviceFinder {

        override fun onResult(list: ArrayList<BLEModel>) {
            mBLEAdapter.setDevices(list)
        }

        override fun onError(errorCode: Int) {

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
                    deviceFinder.checkFeature()
                }
                else {
                    Toast.makeText(mActivity, "The Location access has been denied!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}