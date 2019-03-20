package com.example.harry.linemonitor.view.contract

import com.example.harry.linemonitor.data.LineHistory

interface LineHistoryContract {

    fun showLoading()
    fun hideLoading()
    fun onSuccess(data: List<LineHistory?>?)
    fun onError(data: String)
}