package com.ibm.cic.kotlin.starterkit.application

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.ibm.cic.kotlin.starterkit.fragments.HomeFragment
import com.ibm.cic.kotlin.starterkit.fragments.MeFragment
import com.ibm.cic.kotlin.starterkit.fragments.TransactionsFragment

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

import com.ibm.cic.kotlin.starterkit.helpers.LogHelper
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    var mFragment: Fragment? = null

    var homeFragment = HomeFragment()
    var meFragment = MeFragment()
    var transFragment = TransactionsFragment()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_camera -> {
                    // Handle the camera action
                }
                R.id.nav_gallery -> {

                }
                R.id.nav_slideshow -> {

                }
                R.id.nav_manage -> {

                }
                R.id.nav_share -> {

                }
                R.id.nav_send -> {

                }
            }

            drawer_layout.closeDrawer(GravityCompat.START)
            true
        }

        tab_bottom.setOnNavigationItemSelectedListener { item ->

            when (item.itemId) {

                R.id.tab_home -> {
                    LogHelper.logString("HOME", this)
                    homeFragment.setText("HOME!")
                    return@setOnNavigationItemSelectedListener loadFragment(homeFragment)
                }
                R.id.tab_me -> {
                    LogHelper.logString("ME", this)
                    meFragment.setText("ME~!")
                    return@setOnNavigationItemSelectedListener loadFragment(meFragment)
                }
                R.id.tab_transactions -> {
                    LogHelper.logString("TRANSACTIONS", this)
                    transFragment.setText("Trans?")
                    return@setOnNavigationItemSelectedListener loadFragment(transFragment)
                }
            }
            return@setOnNavigationItemSelectedListener loadFragment(homeFragment)
        }

        loadFragment(homeFragment)

        //initBluetooth()
    }

    override fun onBackPressed() {

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }

    }

    /**
     * Load fragment
     */
    private fun loadFragment(fragment: Fragment): Boolean {

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment).commit()

        return true
    }

    private var mScanFilters: List<ScanFilter>? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mScanner: BluetoothLeScanner? = null
    private var mScanSettings: ScanSettings? = null
    private var mHandler: Handler? = null
    private val SCAN_PERIOD: Long = 2000
    private val REQUEST_CODE_ACCESS_FINE_LOCATION: Int = 1

    public fun initBluetooth() {

        mHandler = Handler()

        initPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun initPermission(access: String) {

        println("### INIT PERMISSION ###")
        val permission = ContextCompat.checkSelfPermission(this, access)

        if (permission == PackageManager.PERMISSION_GRANTED) {
            initBLE()
        }
        else {
            // Manifest.permission.ACCESS_COARSE_LOCATION
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, access)) {

                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setMessage("You have previously denied the Location access, would you want to verify the access?").setPositiveButton("Yes") { dialog, which ->

                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE_ACCESS_FINE_LOCATION)
                }.setNegativeButton("No") { dialog, which ->

                    Toast.makeText(this, "You still DO DOT have the access to the Location Service as you denied the permission request!", Toast.LENGTH_SHORT).show()
                }
                builder.show()
            }
            else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE_ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun initBLE() {

        println("### INIT BLE ###")
        if(packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {

            val bluetoothManager: BluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            mBluetoothAdapter = bluetoothManager.adapter
            homeFragment.deviceList = ArrayList()

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
            print("### NO BLE SUPPORT ###")
            Toast.makeText(this, "The BLE is not supported!", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(this, "The Location access has been granted.", Toast.LENGTH_SHORT).show()
                    initBLE()
                }
                else {
                    Toast.makeText(this, "The Location access has been denied!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onResume() {

        println("### ON RESUME ###")
        super.onResume()

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
                homeFragment.deviceList?.add(device)
                homeFragment.setText("LOADING......")
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
