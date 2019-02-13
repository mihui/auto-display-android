package com.ibm.cic.kotlin.starterkit.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ibm.cic.kotlin.starterkit.application.R
import com.ibm.cic.kotlin.starterkit.interfaces.TextInterface

class TransactionsFragment: Fragment(), TextInterface {

    override fun setText(txt: String) {

        print("### SET TEXT ###")
        val textView: TextView? = view?.findViewById<TextView>(R.id.transText)
        textView?.text = txt
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_transactions, null)
    }

}