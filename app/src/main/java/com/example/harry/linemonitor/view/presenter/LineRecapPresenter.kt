package com.example.harry.linemonitor.view.presenter

import com.example.harry.android_water_leak_app.api.Api
import com.example.harry.linemonitor.view.contract.LienRecapContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class LineRecapPresenter(model: LienRecapContract) {

    var model = model


    fun retriveLineRecap(lineId: String) {
        model.showLoading()

        Api().getInstance().getLineRecap(lineId).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onNext = { lineChartRecap ->
                            model.onSuccess(lineChartRecap)
                            model.hideLoading()
                        },
                        onError = { t: Throwable? -> model.onError(t?.localizedMessage!!) }
                )


    }
}

