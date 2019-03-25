package com.example.harry.linemonitor.view.contract

import com.example.harry.linemonitor.data.NodeMaster

interface NodeListContract {

    fun showLoading()
    fun hideLoading()
    fun onSuccess(data: List<NodeMaster?>?)
    fun onError(data: String)
}