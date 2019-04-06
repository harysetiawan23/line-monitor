package com.example.harry.linemonitor.helper

import android.util.Log
import android.view.ViewGroup
import android.widget.ListView




object Helper {
    fun getListViewSize(myListView: ListView) {
        val listAdapter = myListView.getAdapter()
                ?: // pre-condition
                return

        var totalHeight = 0
        for (i in 0 until listAdapter.getCount()) {
            val listItem = listAdapter.getView(i, null, myListView)
            listItem.measure(0, 0)
            totalHeight += listItem.getMeasuredHeight()
        }

        val params = myListView.getLayoutParams()
        params.height = totalHeight + myListView.getDividerHeight() * (listAdapter.getCount() - 1)
        myListView.setLayoutParams(params)
        myListView.requestLayout()
    }
}