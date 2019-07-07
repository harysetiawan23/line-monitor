package com.example.harry.linemonitor.view.presenter

import android.content.Context
import com.example.harry.android_water_leak_app.api.Api
import com.example.harry.linemonitor.view.contract.LeakageMasterContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class LeakageMasterPresenter(context: Context, model: LeakageMasterContract) {
    private val model = model
    private val context = context

    fun getLeakage(lineId: String) {
        model.showLoading()
        Api().getInstance(context).getLeakage(lineId).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onNext = {
                            model.hideLoading()
                            model.onRetriveDataSuccess(it)

                        },
                        onError = { t: Throwable? ->
                            model.hideLoading()
                            model.onError(t?.message!!.toString())
                        }
                )
    }
}