package com.example.harry.linemonitor.view.contract

import com.example.harry.linemonitor.data.LeakageMaster
import com.example.harry.linemonitor.data.LineLeakageResponse


interface LeakageMasterContract {

    fun showLoading()
    fun hideLoading()
    fun onRetriveDataSuccess(data: List<LineLeakageResponse?>?)
    fun onRefreshData(data: LeakageMaster?)
    fun onUpdateSuccess(data: LeakageMaster?)
    fun onPostSuccess(data: LeakageMaster?)
    fun onDeleteSuccess(data: String)
    fun onError(data: String)
}