package com.ibm.cic.kotlin.starterkit.fragments

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ibm.cic.kotlin.starterkit.application.R
import com.ibm.cic.kotlin.starterkit.interfaces.TextInterface

class MeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        println("### MeFragment.onCreateView ###")

        return inflater.inflate(R.layout.fragment_me, null)
    }

}