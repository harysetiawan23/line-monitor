package com.example.harry.linemonitor.view.presenter

import android.content.Context
import com.example.harry.android_water_leak_app.api.Api
import com.example.harry.linemonitor.data.NodeMaster
import com.example.harry.linemonitor.view.contract.NodeMasterContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class NodeMasterPresenter(context: Context, model: NodeMasterContract) {

    private val model = model
    private val context = context


    //Get All Node UserData
    fun retriveNodeFromServer(){
        model.showLoading()

        Api().getInstance(context).getNodeList().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onNext = { t: List<NodeMaster?>? ->
                            model.hideLoading()
                            model.onRetriveNodeListSuccess(t)
                        },
                        onError = { t: Throwable? -> model.onError(t?.localizedMessage!!) }
                )


    }


    //Store Node UserData
    fun storeNode(nodeMaster: NodeMaster?) {
        model.showLoading()


        Api().getInstance(context).storeNodeMaster(
                nodeMaster!!.sn!!,
                nodeMaster!!.phoneNumber!!,
                nodeMaster!!.lat!!,
                nodeMaster!!.lng!!,
                nodeMaster!!.isStartNode!!.toString(),
                nodeMaster!!.pressOffset!!.toString(),
                nodeMaster!!.liquidFlowKonstanta!!.toString(),
                nodeMaster!!.flowRateModel!!.toString(),
                nodeMaster!!.pressureTranducerModel!!.toString()


        ).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onComplete = {

                        },
                        onNext = { nodeMaster ->
                            model.hideLoading()
                            model.onSubmitSuccess(nodeMaster)


                        },
                        onError = { throwable ->
                            model.hideLoading()
                            model.onError(throwable.localizedMessage)
                        }
                )
    }


    //Submit update Node UserData
    fun updateNodeData(nodeMaster: NodeMaster?) {
        model.showLoading()


        Api().getInstance(context).updateNodeMester(
                nodeMaster!!.id!!.toString(),
                nodeMaster!!.sn!!,
                nodeMaster!!.phoneNumber!!,
                nodeMaster!!.lat!!,
                nodeMaster!!.lng!!,
                nodeMaster!!.isStartNode!!.toString(),
                nodeMaster!!.pressOffset!!.toString(),
                nodeMaster!!.liquidFlowKonstanta!!.toString(),
                nodeMaster!!.flowRateModel!!.toString(),
                nodeMaster!!.pressureTranducerModel!!.toString()

        ).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onComplete = {

                        },
                        onNext = { nodeMaster ->
                            model.hideLoading()
                            model.onUpdate(nodeMaster)
                        },
                        onError = { throwable ->
                            model.hideLoading()
                            model.onError(throwable.localizedMessage)
                        }
                )
    }


    //Refresh Node UserData
    fun refreshNodeData(data: NodeMaster) {
        model.showLoading()
        Api().getInstance(context).getNodeById(data.id.toString()).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onNext = { nodeMaster ->
                            model.onUpdate(nodeMaster)
                            model.hideLoading()
                        }
                )
    }

    //Delete Node UserData
    fun deleteNodeData(nodeMaster: NodeMaster?) {
        model.showLoading()


        Api().getInstance(context).deleteNodeMaster(
                nodeMaster!!.id!!.toString()

        ).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onComplete = {

                        },
                        onNext = { ResponseDeleteNodeSuccess ->
                            model.hideLoading()
                            model.onDeleteSuccess(ResponseDeleteNodeSuccess)


                        },
                        onError = { throwable ->
                            model.hideLoading()
                            model.onError(throwable.localizedMessage)
                        }
                )
    }
}

