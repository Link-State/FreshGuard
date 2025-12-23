package com.example.caps_project

import com.example.caps_project.interfaces.API
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {
    var api: API = Retrofit.Builder()
        .baseUrl("http://34.10.101.220:8000/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(API::class.java)

    var retry : Int = 60
}