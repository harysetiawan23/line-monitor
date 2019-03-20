package com.example.harry.linemonitor.view.presenter

import com.example.harry.android_water_leak_app.api.Api
import com.example.harry.linemonitor.data.LineMasterMap
import com.example.harry.linemonitor.view.contract.LineMapContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class LineMapsPresenter(model: LineMapContract) {

    var model = model


    fun retriveLineFromServer() {
        model.showLoading()

        Api().getInstance().getLineMasterLinesMaps().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onNext = { t: List<LineMasterMap>? ->
                            model.hideLoading()
                            model.onSuccess(t)
                        },
                        onError = { t: Throwable? -> model.onError(t?.localizedMessage!!) }
                )


    }
}

