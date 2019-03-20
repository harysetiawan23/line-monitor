package com.example.harry.linemonitor.view.presenter

import com.example.harry.android_water_leak_app.api.Api
import com.example.harry.linemonitor.data.LineMaster
import com.example.harry.linemonitor.view.contract.LineListContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class LineListPresenter(model:LineListContract){

    var model = model


    fun retriveLineFromServer(){
        model.showLoading()

        Api().getInstance().getLineMasterLines().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onNext = { t: List<LineMaster>? ->
                            model.hideLoading()
                            model.onSuccess(t)
                        },
                        onError = { t: Throwable? -> model.onError(t?.localizedMessage!!) }
                )



    }
}

