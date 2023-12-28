package com.anggun_chintya.vetric

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anggun_chintya.vetric.APIWeather.ApiService
import com.anggun_chintya.vetric.APIWeather.RetrofitClient
import com.anggun_chintya.vetric.DataWeather.WeatherApp
import com.anggun_chintya.vetric.databinding.FragmentBerandaBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BerandaFragment : Fragment() {
    private lateinit var binding: FragmentBerandaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBerandaBinding.inflate(layoutInflater)

        fetchWeatherData()

        return binding.root
    }

    private fun fetchWeatherData(){
        val apiService = RetrofitClient.retrofit.create(ApiService::class.java)

        val call = apiService.getWeatherData("pekanbaru","2a748269a8d726dfc7514a3391ec8e54","metric")

        call.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val suhu = responseBody.main.temp.toString()
                    binding.tvSuhuCuaca.setText(suhu + " °C")
                    Log.d("SUHU","onResponse: $suhu")
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
}