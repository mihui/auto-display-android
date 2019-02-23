package com.ibm.cic.kotlin.starterkit.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ibm.cic.kotlin.starterkit.application.R

class TransactionsFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        println("### TransactionsFragment.onCreateView ###")

        return inflater.inflate(R.layout.fragment_transactions, null)
    }

}