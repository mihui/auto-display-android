package com.ibm.cic.kotlin.starterkit.adapters

import android.support.v7.widget.RecyclerView
import com.ibm.cic.kotlin.starterkit.models.BLEModel

import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ibm.cic.kotlin.starterkit.application.R
import com.ibm.cic.kotlin.starterkit.interfaces.OnBLEItemClickInterface

import kotlinx.android.synthetic.main.item_connected_ble.view.*
import kotlinx.android.synthetic.main.item_discovering_ble.view.*

internal class BLEAdapter constructor(_itemLayout: Int, _itemListener: OnBLEItemClickInterface) : Adapter<RecyclerView.ViewHolder>() {

    private lateinit var devices: List<BLEModel>
    private var itemListener: OnBLEItemClickInterface? = null
    private var itemLayout: Int

    init {

        setDevices(ArrayList())
        itemListener = _itemListener
        itemLayout = _itemLayout
    }

    fun setDevices(_devices: List<BLEModel>) {
        devices = _devices;
        notifyDataSetChanged();
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val model = devices.get(position)

        if(itemLayout == R.layout.item_connected_ble) {
            holder.itemView.connected_name.text = model.name
        }
        else {
            holder.itemView.discovered_name.text = model.name
            holder.itemView.discovered_address.text = model.address
            holder.itemView.discovered_rssi.text = model.rssi.toString()
        }

        holder.itemView.setOnClickListener {

            itemListener?.onClick(model)
        }
    }

    override fun getItemCount(): Int {

        return devices.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {

        val view: View = LayoutInflater.from(parent.context).inflate(itemLayout, parent, false)

        return RecyclerViewHolder(view)
    }


    inner class RecyclerViewHolder(view: View): RecyclerView.ViewHolder(view) {

    }
}