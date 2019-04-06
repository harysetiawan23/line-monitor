package com.example.harry.linemonitor.view.presenter

import com.example.harry.android_water_leak_app.api.Api
import com.example.harry.linemonitor.data.LineRecap
import com.example.harry.linemonitor.view.contract.LineRecordHourlyContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class LineRecordHourlyPresenter(model: LineRecordHourlyContract) {

    var model = model


    fun getLineHistory(lineId: String) {

        Api().getInstance().getLineRecordHourly(lineId).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onNext = { list: List<LineRecap?>? ->
                            model.onRecordHourlyGeted(list)
                        },
                        onError = { t: Throwable? -> }
                )


    }
}

