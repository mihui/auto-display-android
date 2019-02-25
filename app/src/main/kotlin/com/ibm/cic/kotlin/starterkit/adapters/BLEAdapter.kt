package com.ibm.cic.kotlin.starterkit.adapters

import android.support.v7.widget.RecyclerView
import com.ibm.cic.kotlin.starterkit.BLEModel

import android.support.v7.widget.RecyclerView.Adapter
import android.view.View
import android.view.ViewGroup

internal class BLEAdapter constructor(_devices: List<BLEModel>) : Adapter<BLEAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {

        return devices.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var devices: List<BLEModel>

    init {
        devices = _devices
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

    }
}