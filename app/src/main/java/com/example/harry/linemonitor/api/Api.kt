package com.example.harry.android_water_leak_app.api


import android.content.Context
import com.example.harry.linemonitor.helper.PreferencesUtility
import com.example.harry.submission_2kade.api.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit



class Api {


    private fun getInit(context:Context):Retrofit {

        var prefrences = PreferencesUtility.getUserData(context)

        val interceptor =  Interceptor { chain ->
            val request = chain.request()?.newBuilder()
                    ?.addHeader("Accept", "application/json")
                    ?.addHeader("Authorization","${prefrences.type} ${prefrences.token}")
                    ?.build()
            chain.proceed(request)
        }

        var okhttp = OkHttpClient.Builder().addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()


        return Retrofit.Builder()
                .client(okhttp)
                .baseUrl("http://13.67.71.80/api/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).build()

    }

    fun getInstance(context: Context):ApiService{
        return getInit(context).create(ApiService::class.java)
    }

}