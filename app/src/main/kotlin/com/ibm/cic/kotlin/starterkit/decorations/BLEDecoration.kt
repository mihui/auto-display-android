package com.ibm.cic.kotlin.starterkit.decorations

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class BLEDecoration constructor(horizontalSpacing: Int, verticalSpacing: Int) : RecyclerView.ItemDecoration() {

    var _horizontalSpacing: Int = 0
    var _verticalSpacing: Int = 0

    init {
        _horizontalSpacing = horizontalSpacing
        _verticalSpacing = verticalSpacing
    }

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {


        outRect?.set(_horizontalSpacing, _verticalSpacing, _horizontalSpacing, _verticalSpacing);
    }
}