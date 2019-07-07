package com.example.harry.linemonitor.view.contract

import com.example.harry.linemonitor.data.LineMaster
import com.example.harry.linemonitor.data.LineMasterMap


interface LineMasterContract {

    fun showLoading()
    fun hideLoading()
    fun onRetriveDataSuccess(data: List<LineMasterMap?>?)
    fun onRefreshData(data:LineMasterMap?)
    fun onUpdateSuccess(data:LineMaster?)
    fun onPostSuccess(data: LineMaster?)
    fun onDeleteSuccess(data:String)
    fun onError(data: String)
}