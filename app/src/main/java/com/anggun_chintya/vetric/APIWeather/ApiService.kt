package com.anggun_chintya.vetric.APIWeather

import com.anggun_chintya.vetric.DataWeather.WeatherApp
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("weather")
    fun  getWeatherData(
        @Query("q") city:String,
        @Query("appid") appid:String,
        @Query("units") units:String
    ): Call<WeatherApp>
}