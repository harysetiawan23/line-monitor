package com.example.harry.linemonitor.view.contract

import com.example.harry.linemonitor.data.SolvedSuccessMessage

interface SolveLeakageContract {
    fun showLoading()
    fun hideLoading()
    fun onUpdateSuccess(data: SolvedSuccessMessage)
    fun onError(data: String)
}