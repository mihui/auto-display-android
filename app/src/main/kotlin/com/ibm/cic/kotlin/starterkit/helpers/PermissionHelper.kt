package com.ibm.cic.kotlin.starterkit.helpers

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import android.support.v4.content.LocalBroadcastManager

class PermissionHelper constructor(activity: Activity){

    private val TAG = "BLE|PermissionHelper"

    private var mActivity: Activity = activity
    private var localBroadcastManager: LocalBroadcastManager

    companion object {

        const val BLE_ACCESS = "BLE-ACCESS"
    }

    init {

        localBroadcastManager = LocalBroadcastManager.getInstance(mActivity)
    }

    fun requestPermission(access: String) {

        Log.i(TAG, "### INIT PERMISSION ###")

        val permission = ContextCompat.checkSelfPermission(mActivity.applicationContext, access)

        if (permission == PackageManager.PERMISSION_GRANTED) {

            println("### ACCESS GRANTED ###")
            val intent = Intent(BLE_ACCESS)

            mActivity.sendBroadcast(intent)
        }
        else {

            Log.e(TAG, "NO ACCESS")
            // Manifest.permission.ACCESS_COARSE_LOCATION
            if(ActivityCompat.shouldShowRequestPermissionRationale(mActivity, access)) {

                val builder = AlertDialog.Builder(mActivity)
                builder.setMessage("You have previously denied the Location access, would you want to verify the access?").setPositiveButton("Yes") { _, _ ->

                    ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), DeviceFinder.REQUEST_CODE_ACCESS_FINE_LOCATION)
                }.setNegativeButton("No") { _, _ ->

                    Toast.makeText(mActivity, "You still DO DOT have the access to the Location Service as you denied the permission request!", Toast.LENGTH_SHORT).show()
                }
                builder.show()
            }
            else {
                ActivityCompat.requestPermissions(mActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), DeviceFinder.REQUEST_CODE_ACCESS_FINE_LOCATION)
            }
        }
    }

}
