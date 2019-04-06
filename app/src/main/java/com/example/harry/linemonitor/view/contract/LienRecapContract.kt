package com.example.harry.linemonitor.view.contract

import com.example.harry.linemonitor.data.LineChartRecap

interface LienRecapContract {

    fun showLoading()
    fun hideLoading()
    fun onSuccess(data: LineChartRecap)
    fun onError(data: String)
}