package com.ibm.cic.kotlin.starterkit.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import com.ibm.cic.kotlin.starterkit.BLEModel

import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ibm.cic.kotlin.starterkit.application.R
import com.ibm.cic.kotlin.starterkit.interfaces.OnBLEItemClickInterface

internal class BLEAdapter constructor(_context: Context) : Adapter<RecyclerView.ViewHolder>() {

    private var context: Context
    private lateinit var devices: List<BLEModel>
    private lateinit var listener: OnBLEItemClickInterface

    init {

        context = _context
        setDevices(ArrayList())
    }

    fun setDevices(_devices: List<BLEModel>) {
        devices = _devices;
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = devices.get(position)
        holder.itemView.setOnClickListener {
            listener.onClick(model)
        }
    }

    override fun getItemCount(): Int {

        return devices.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {

        val view: View = LayoutInflater.from(context).inflate(R.layout.item_ble, parent, false)

        return RecyclerViewHolder(view)
    }


    inner class RecyclerViewHolder(view: View): RecyclerView.ViewHolder(view) {


    }
}