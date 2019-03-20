package com.example.harry.linemonitor.view.presenter

import android.util.Log
import com.example.harry.android_water_leak_app.api.Api
import com.example.harry.linemonitor.data.LineHistory
import com.example.harry.linemonitor.data.LineMasterMap
import com.example.harry.linemonitor.view.contract.LineHistoryContract
import com.example.harry.linemonitor.view.contract.LineMapContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class LineHistoryPresenter(model: LineHistoryContract) {

    var model = model


    fun getLineHistory(lineId:String) {
        model.showLoading()

        Api().getInstance().getLineHistory(lineId).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onNext = {
                            list: List<LineHistory>? ->
                            model.hideLoading()
                            model.onSuccess(list)
                        },
                        onError = { t: Throwable? -> }
                )


    }
}

