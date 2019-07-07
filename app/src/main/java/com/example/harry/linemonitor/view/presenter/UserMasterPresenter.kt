package com.example.harry.linemonitor.view.presenter

import android.content.Context
import android.util.Log
import com.example.harry.android_water_leak_app.api.Api
import com.example.harry.linemonitor.data.RegisterResponse
import com.example.harry.linemonitor.view.contract.UserMasterContract
import com.example.harry.linemonitor.view.contract.UserRegisterContact
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class UserMasterPresenter(context: Context) {
    private val context = context

    fun userLogin(model: UserMasterContract, email: String, password: String, fcmToken: String) {
        var loginModel = model
        loginModel.showLoading()
        Api().getInstance(context).login(email, password, fcmToken).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribeBy(
                        onNext = { loginResponse ->
                            loginModel.hideLoading()
                            loginModel.onLoginSuccess(loginResponse)

                        },
                        onError = { t: Throwable? ->
                            loginModel.hideLoading()
                            loginModel.onError(t?.message?.toString())
                        }
                )
    }

    fun userRegister(model: UserRegisterContact, email: String, password: String, c_password: String, name: String) {
        Log.d("REGISTER_DATA",email+password+name+c_password)
        var registerModel = model
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