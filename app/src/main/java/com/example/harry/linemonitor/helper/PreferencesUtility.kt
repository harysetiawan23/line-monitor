package com.example.harry.linemonitor.helper

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.example.harry.linemonitor.data.Data
import com.example.harry.linemonitor.data.LoginResponse
import com.example.harry.linemonitor.data.RegisterResponse
import com.example.harry.linemonitor.data.UserData

object PreferencesUtility {

    val PREFFRENCE = "lineMaster"
    val LOGGED_IN_PREF = "logged_in_status"
    val USER_NAME = "name"
    val USER_EMAIL = "email"
    val USER_PASSWORD = "password"
    val USER_ID = "id"
    val USER_TOKEN_TYPE = "type"
    val USER_TOKEN = "token"


    private lateinit var sharedPrefrences: SharedPreferences
    private lateinit var prefrencesEditor: SharedPreferences.Editor

    fun saveUsetDataFromRegister(context: Context,registerResponse: RegisterResponse){
        sharedPrefrences = context.getSharedPreferences(PREFFRENCE, MODE_PRIVATE)
        prefrencesEditor = sharedPrefrences.edit()


        prefrencesEditor.putString(USER_NAME, registerResponse.data!!.name)
        prefrencesEditor.putString(USER_EMAIL, registerResponse.data!!.email)
        prefrencesEditor.putString(USER_TOKEN, registerResponse.token)
        prefrencesEditor.putString(USER_TOKEN_TYPE,"Bearer")
        prefrencesEditor.commit()
    }


    fun saveUserData(context: Context, loginResponse: LoginResponse) {
        sharedPrefrences = context.getSharedPreferences(PREFFRENCE, MODE_PRIVATE)
        prefrencesEditor = sharedPrefrences.edit()


        prefrencesEditor.putString(USER_NAME, loginResponse.data!!.name)
        prefrencesEditor.putString(USER_EMAIL, loginResponse.data!!.email)
        prefrencesEditor.putString(USER_TOKEN, loginResponse.token)
        prefrencesEditor.putString(USER_TOKEN_TYPE,loginResponse.type)
        prefrencesEditor.commit()


    }


    fun getUserData(context: Context): LoginResponse {
        sharedPrefrences = context.getSharedPreferences(PREFFRENCE, MODE_PRIVATE)
        prefrencesEditor = sharedPrefrences.edit()


        val token = sharedPrefrences.getString(USER_TOKEN,"")
        val email = sharedPrefrences.getString(USER_EMAIL,"")
        val name = sharedPrefrences.getString(USER_NAME,"")
        val type = sharedPrefrences.getString(USER_TOKEN_TYPE,"")

        val data = Data(email = email,name = name)
        return LoginResponse(token = token,data = data,type = type)
    }


    fun logout(context: Context){
        sharedPrefrences = context.getSharedPreferences(PREFFRENCE, MODE_PRIVATE)
        prefrencesEditor = sharedPrefrences.edit()

        prefrencesEditor.clear()
        prefrencesEditor.commit()
    }

}