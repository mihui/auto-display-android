package com.ibm.cic.kotlin.starterkit.application

import android.bluetooth.BluetoothClass
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.ibm.cic.kotlin.starterkit.fragments.HomeFragment
import com.ibm.cic.kotlin.starterkit.fragments.MeFragment
import com.ibm.cic.kotlin.starterkit.fragments.TransactionsFragment
import com.ibm.cic.kotlin.starterkit.helpers.DeviceManager
import com.ibm.cic.kotlin.starterkit.helpers.LogHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity() {

    private var homeFragment = HomeFragment()
    private var meFragment = MeFragment()
    private var transFragment = TransactionsFragment()

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

            val bundle: Bundle = Bundle();

            when (item.itemId) {

                R.id.tab_home -> {
                    LogHelper.logString("HOME", this)

                    bundle.putString("TEXT", "HOME")

                    return@setOnNavigationItemSelectedListener loadFragment(homeFragment, bundle)
                }
                R.id.tab_me -> {
                    LogHelper.logString("ME", this)
                    bundle.putString("TEXT", "ME")

                    return@setOnNavigationItemSelectedListener loadFragment(meFragment, bundle)
                }
                R.id.tab_transactions -> {
                    LogHelper.logString("TRANSACTIONS", this)
                    bundle.putString("TEXT", "TRANS")

                    return@setOnNavigationItemSelectedListener loadFragment(transFragment, bundle)
                }
            }
            return@setOnNavigationItemSelectedListener loadFragment(homeFragment, bundle)
        }

        val bundle = Bundle();
        bundle.putString("TEXT", "HOME")
        loadFragment(homeFragment, bundle)

        initService()

        //initBluetooth()
    }

    private val bluetoothServiceBroadCastReceiver: BroadcastReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {

            println("### BROADCAST RECEIVER: ${intent?.action}")

            when(intent?.action) {

                DeviceManager.DEVICE_CONNECTED -> {

                }
                DeviceManager.DEVICE_DISCONNECTED -> {

                }
                DeviceManager.DEVICE_DISCOVERED -> {

                }
                DeviceManager.DEVICE_DATA_AVAILABLE -> {

                }
                DeviceManager.DEVICE_NOT_SUPPORTED -> {

                }
            }
        }
    }

    private fun bluetoothServiceIntentFilter (): IntentFilter {

        val intentFilter = IntentFilter()
        intentFilter.addAction(DeviceManager.DEVICE_CONNECTED)
        intentFilter.addAction(DeviceManager.DEVICE_DISCONNECTED)
        intentFilter.addAction(DeviceManager.DEVICE_DISCOVERED)
        intentFilter.addAction(DeviceManager.DEVICE_DATA_AVAILABLE)
        intentFilter.addAction(DeviceManager.DEVICE_NOT_SUPPORTED)
        return intentFilter
    }

    private fun initService() {

        LocalBroadcastManager.getInstance(this).registerReceiver(bluetoothServiceBroadCastReceiver, bluetoothServiceIntentFilter())
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
    private fun loadFragment(fragment: Fragment, @Nullable bundle: Bundle): Boolean {

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        fragment.arguments = bundle
        transaction.commit()
        return true
    }


}
