package com.example.harry.linemonitor.view.presenter

import android.content.Context
import com.example.harry.android_water_leak_app.api.Api
import com.example.harry.linemonitor.view.contract.SolveLeakageContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class LeakSuccessPresenter(context: Context, model: SolveLeakageContract) {
    private val model = model
    private val context = context

    fun updateLeakageIntoSuccess(leakageId: String) {
        model.showLoading()
        Api().getInstance(context).solveLeakage(leakageId).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onNext = {
                            model.hideLoading()
                        },
                        onError = { t: Throwable? ->
                            model.hideLoading()
                            model.onError(t?.message!!.toString())
                        }
                )
    }
}