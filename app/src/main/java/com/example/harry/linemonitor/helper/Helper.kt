package com.example.harry.linemonitor.helper

import android.util.Log
import android.view.ViewGroup
import android.widget.ListView




object Helper {
    fun getListViewSize(myListView: ListView) {
        val listAdapter = myListView.adapter
                ?: // pre-condition
                return

        var totalHeight = 0
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, myListView)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }

        val params = myListView.layoutParams
        params.height = totalHeight + myListView.dividerHeight * (listAdapter.count - 1)
        myListView.layoutParams = params
        myListView.requestLayout()
    }
}