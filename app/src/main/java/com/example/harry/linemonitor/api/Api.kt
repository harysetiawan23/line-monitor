package com.example.harry.android_water_leak_app.api


import com.example.harry.submission_2kade.api.ApiService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit



class Api {


    fun getInit():Retrofit {

        val interceptor =  Interceptor { chain ->
            val request = chain.request()?.newBuilder()
                    ?.addHeader("Accept", "application/json")
                    ?.addHeader("Authorization","Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjFjODExMWIzNDRlYTljMjU1NjY1ZWYzZjczMzQ5MmUzZTE0Y2RmZWZjM2FlNmM5ZWJjNTg1N2Y5YmFiZGJhNDIxNDQxZWEzZmE3YjdmZjkzIn0.eyJhdWQiOiIzIiwianRpIjoiMWM4MTExYjM0NGVhOWMyNTU2NjVlZjNmNzMzNDkyZTNlMTRjZGZlZmMzYWU2YzllYmM1ODU3ZjliYWJkYmE0MjE0NDFlYTNmYTdiN2ZmOTMiLCJpYXQiOjE1NTI0NDM4NDUsIm5iZiI6MTU1MjQ0Mzg0NSwiZXhwIjoxNTg0MDY2MjQ1LCJzdWIiOiIxIiwic2NvcGVzIjpbXX0.Z87cqY3AnBl_6XXA6vKawkJO3_JCqHufBOQmFxTbNpVpgAuARgwG7A8nrSY0SxxfwL3xVSSZkaeRLKN3c8Qrd-YFaAZ_TGIPTwrcRNXEBpNNAlVE4BOkmJ-kdQRZtMPniWRrlYhZiOZfuDevlWF3yI-otuRYNAXyyRwepjpZM4fhOL4ESR0ihJlWNqqRUI_LwOGT2vC_Wn4pEwHkUHIg2TvRokw0S0LpS64yLr5W0-rR5_PNTY0YVOZv_DCkSViYqhvQEJZ9UKZ9Dt2c_2WQVy_qJu5zeGYn5uqnSzNXNFd_WMfTdg03IyRM7oKfNgomOQjK4pOosC8vgOuKYDo9r_diLe_Yo_HQBkTdGtMp3FUjivpaAirGe1_X2EZpLQhgqW9SWcCQ-JLVDVB28GE2egNTOpVOVe1P-aDNTz3SFq7a7zpyqxpeF-PhJ-TC0ZNzuj4AxkyIICnD3n3MOPdCdXdzW5DfDtvM6vW-fRLb6dZd1MDxLA8KhxW86dxn5LSVXdt8GrIrva4iEDb8npIjEWNtx6zVAofC47XoJf0AzWvFkkUrlZJQOp-pAI1vqQgz8dp46_Chy2PMQ5mpQWpyCqnamXSUyhzAvRejxfkdStJF7cDMvYr6yBCBUDMWC2dCBMLhzP2eviLhX9OgfOc9XunZEwVFIte_y0xDqtobF98")
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
                .baseUrl("http://104.215.153.249/api/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create()).build()

    }

    fun getInstance():ApiService{
        return getInit().create(ApiService::class.java)
    }

}