package com.ibm.cic.kotlin.starterkit.fragments

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ibm.cic.kotlin.starterkit.application.R
import com.ibm.cic.kotlin.starterkit.interfaces.TextInterface


class HomeFragment : Fragment(), TextInterface {

    override fun setText(txt: String) {

        print("### SET TEXT ###")
        val textView:TextView? = view?.findViewById<TextView>(R.id.homeText)
        textView?.text = txt
    }

    var deviceList: ArrayList<BluetoothDevice>? = null
    get() = field
    set(value) {
        field = value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_home, null)
    }

}