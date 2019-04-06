package com.example.harry.linemonitor.view.contract

import com.example.harry.linemonitor.data.LineRecap

interface LineRecordHourlyContract {

    fun onRecordHourlyGeted(data: List<LineRecap?>?)
    fun onError(data: String)
}