package com.example.harry.linemonitor.view.presenter

import android.content.Context
import android.util.Log
import com.example.harry.android_water_leak_app.api.Api
import com.example.harry.linemonitor.data.RegisterResponse
import com.example.harry.linemonitor.view.contract.UserRegisterContact
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class UserRegister(context: Context, model: UserRegisterContact) {
    private val context = context

    private val registerModel = model

    fun userRegister(email: String, password: String, c_password: String, name: String) {
        Log.d("REGISTER_DATA", email + password + name + c_password)

        registerModel.showLoading()
        Api().getInstance(context).register(name, email, password, c_password).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onNext = { registerResponse: RegisterResponse? ->
                            registerModel.onRegisterSuccess(registerResponse)
                        },
                        onError = { t: Throwable? ->
                            registerModel.hideLoading()
                            registerModel.onError(t?.message?.toString())
                        }
                )
    }
}