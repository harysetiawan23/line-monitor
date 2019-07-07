package com.example.harry.linemonitor.view.contract

import com.example.harry.linemonitor.data.NodeMaster
import com.example.harry.linemonitor.data.ResponseDeleteSuccess

interface NodeMasterContract {

    fun showLoading()
    fun hideLoading()
    fun onRetriveNodeListSuccess(data: List<NodeMaster?>?)
    fun onUpdate(data:NodeMaster)
    fun onDeleteSuccess(data:ResponseDeleteSuccess)
    fun onSubmitSuccess(data:NodeMaster)
    fun onError(data: String)
}