package com.example.harry.linemonitor.view.contract

import com.example.harry.linemonitor.data.LineMasterMap


interface LineMapContract {

    fun showLoading()
    fun hideLoading()
    fun onSuccess(data: List<LineMasterMap?>?)
    fun onError(data: String)
}