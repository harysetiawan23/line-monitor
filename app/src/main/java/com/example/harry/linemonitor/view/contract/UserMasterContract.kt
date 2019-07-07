package com.example.harry.linemonitor.view.contract

import com.example.harry.linemonitor.data.LoginResponse
import com.example.harry.linemonitor.data.RegisterResponse

interface UserMasterContract {
    fun showLoading()
    fun hideLoading()
    fun onLoginSuccess(data:LoginResponse?)
    fun onError(data:String?)

}

interface UserRegisterContact{
    fun showLoading()
    fun hideLoading()
    fun onRegisterSuccess(data:RegisterResponse?)
    fun onError(data:String?)
}