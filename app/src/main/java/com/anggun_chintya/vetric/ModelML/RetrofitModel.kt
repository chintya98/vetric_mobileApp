package com.anggun_chintya.vetric.ModelML

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitModel {
    private const val BASE_URL = "https://chintyaang.pythonanywhere.com"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}