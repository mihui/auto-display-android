package com.ibm.cic.kotlin.starterkit.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import com.ibm.cic.kotlin.starterkit.BLEModel

import android.support.v7.widget.RecyclerView.Adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ibm.cic.kotlin.starterkit.application.R
import com.ibm.cic.kotlin.starterkit.interfaces.OnBLEItemClickInterface
import kotlinx.android.synthetic.main.item_ble.view.*

internal class BLEAdapter constructor() : Adapter<RecyclerView.ViewHolder>() {

    private lateinit var devices: List<BLEModel>
    private var listener: OnBLEItemClickInterface? = null

    init {

        setDevices(ArrayList())
    }

    fun setDevices(_devices: List<BLEModel>) {
        devices = _devices;
        notifyDataSetChanged();
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val model = devices.get(position)

        holder.itemView.name.text = model.name
        holder.itemView.address.text = model.address

        holder.itemView.setOnClickListener {

            listener?.onClick(model)
        }
    }

    override fun getItemCount(): Int {

        return devices.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {

        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_ble, parent, false)

        return RecyclerViewHolder(view)
    }


    inner class RecyclerViewHolder(view: View): RecyclerView.ViewHolder(view) {


    }
}