package com.anggun_chintya.vetric.ModelML

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


interface APIModel {
    @POST("/predict")
    fun getPredict(@Body inputData: FeaturesModel): Call<TargetModel>
}