package com.example.harry.linemonitor.view.contract

import com.example.harry.linemonitor.data.LineMaster

interface LineListContract {

    fun showLoading()
    fun hideLoading()
    fun onSuccess(data:List<LineMaster?>?)
    fun onError(data:String)
}