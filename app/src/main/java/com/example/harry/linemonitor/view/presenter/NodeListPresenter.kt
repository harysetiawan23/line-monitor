package com.example.harry.linemonitor.view.presenter

import com.example.harry.android_water_leak_app.api.Api
import com.example.harry.linemonitor.data.LineMaster
import com.example.harry.linemonitor.data.NodeMaster
import com.example.harry.linemonitor.view.contract.LineListContract
import com.example.harry.linemonitor.view.contract.NodeListContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class NodeListPresenter(model:NodeListContract){

    var model = model


    fun retriveNodeFromServer(){
        model.showLoading()

        Api().getInstance().getNodeList().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onNext = { t: List<NodeMaster?>? ->
                            model.hideLoading()
                            model.onSuccess(t)
                        },
                        onError = { t: Throwable? -> model.onError(t?.localizedMessage!!) }
                )



    }
}

