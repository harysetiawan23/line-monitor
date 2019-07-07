package com.example.harry.linemonitor.view.presenter

import android.content.Context
import android.util.Log
import com.example.harry.android_water_leak_app.api.Api
import com.example.harry.linemonitor.data.LineMaster
import com.example.harry.linemonitor.data.LineMasterMap
import com.example.harry.linemonitor.data.NodeMaster
import com.example.harry.linemonitor.view.contract.LineMasterContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class LineMasterPresenter(context: Context,model: LineMasterContract) {

    private val model = model
    private val context = context


    fun retriveLineFromServer() {
        model.showLoading()

        Api().getInstance(context).getLineMasterLinesMaps().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onNext = { t: List<LineMasterMap>? ->
                            model.hideLoading()
                            model.onRetriveDataSuccess(t)
                        },
                        onError = { t: Throwable? -> model.onError(t?.message!!) }
                )


    }

    fun refreshLineMaster(id: String) {
        model.showLoading()
        Api().getInstance(context).getLineMasterLineMpasById(id).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onNext = { lineMasterMap ->
                            model.onRefreshData(lineMasterMap.get(0))
                            Log.d("LineMaster",lineMasterMap.toString())
                            model.hideLoading()
                        }
                )
    }




    fun postLineMaster(lineMaster: LineMaster) {
        model.showLoading()



        Api().getInstance(context).storeLineMaster(
                lineMaster.name!!,
                lineMaster.start!!,
                lineMaster.end!!,
                lineMaster.startNodeId!!,
                lineMaster.endNodeId!!,
                lineMaster.distance!!,
                lineMaster.diameter!!,
                lineMaster.thicknes!!,
                lineMaster.manufacture!!
        ).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onNext = { lineMaster ->
                            model.hideLoading()
                            model.onPostSuccess(lineMaster)
                        },
                        onError = { throwable -> model.onError(throwable.message!!) }
                )

    }


    fun updateLineMaster(lineMaster: LineMaster) {
        model.showLoading()



        Api().getInstance(context).updateLineMaster(
                lineMaster.id!!.toString(),
                lineMaster.name!!,
                lineMaster.start!!,
                lineMaster.end!!,
                lineMaster.startNodeId!!,
                lineMaster.endNodeId!!,
                lineMaster.distance!!,
                lineMaster.diameter!!,
                lineMaster.thicknes!!,
                lineMaster.manufacture!!
        ).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onNext = { lineMaster ->
                            model.hideLoading()
                            model.onUpdateSuccess(lineMaster)
                        },
                        onError = { throwable -> model.onError(throwable.message!!) }
                )

    }

    fun deleteLineMaster(id: String) {

        model.showLoading()


        Api().getInstance(context).deleteLineMaster(
               id

        ).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onComplete = {

                        },
                        onNext = { responseDeleteSuccess ->

                            model.hideLoading()
                            model.onDeleteSuccess(responseDeleteSuccess.toString())



                        },
                        onError = { throwable ->
                            model.hideLoading()
                            model.onError(throwable.localizedMessage)
                        }
                )





    }

}

