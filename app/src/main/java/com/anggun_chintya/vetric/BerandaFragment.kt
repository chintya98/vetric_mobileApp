package com.anggun_chintya.vetric

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.anggun_chintya.vetric.APIWeather.ApiService
import com.anggun_chintya.vetric.APIWeather.RetrofitClient
import com.anggun_chintya.vetric.Catatan.RecyclerAdapter_Catatan
import com.anggun_chintya.vetric.DataWeather.Weather
import com.anggun_chintya.vetric.DataWeather.WeatherApp
import com.anggun_chintya.vetric.Elektronik.DataElektronik
import com.anggun_chintya.vetric.Elektronik.RecyclerAdapter_Elektronik
import com.anggun_chintya.vetric.ModelML.APIModel
import com.anggun_chintya.vetric.ModelML.FeaturesModel
import com.anggun_chintya.vetric.ModelML.RetrofitModel
import com.anggun_chintya.vetric.ModelML.TargetModel
import com.anggun_chintya.vetric.Rekomendasi.DataRekomendasi
import com.anggun_chintya.vetric.Rekomendasi.RecyclerAdapter_Rekomendasi
import com.anggun_chintya.vetric.databinding.DetailPrediksiMlBinding
import com.anggun_chintya.vetric.databinding.FragmentBerandaBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class BerandaFragment : Fragment() {
    private lateinit var binding: FragmentBerandaBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var detailPrediksi : DetailPrediksiMlBinding
    private lateinit var auth: FirebaseAuth

    private lateinit var rekomendasiAdapter: RecyclerAdapter_Rekomendasi

    private var suhu=0.0
    private var curahHujan=276.0
    private var sunshine=0.0
    private var kelembaban=0
    private var hasilPrediksi = 0.0
    private var totalDaya=0.0

    val decimalFormat = DecimalFormat("#,##0.00")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBerandaBinding.inflate(layoutInflater)

        getNamaUser()
        fetchWeatherData()
        tampilKalkulasi()
        prediksiDaya_wCuaca(curahHujan, suhu, sunshine, kelembaban)

        binding.cardKonsumsiDayaCuaca.setOnClickListener {
            tampilDetailPrediksi()
        }

        getDataRekomendasi()

        return binding.root
    }

    private fun tampilKalkulasi(){
        databaseReference = FirebaseDatabase.getInstance().getReference("elektronik")
        val auth= FirebaseAuth.getInstance()
        var userID = auth.currentUser?.uid

        var totalBiaya = 0.0;

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (elektronikSnapshot in dataSnapshot.children) {
                    val elektronik = elektronikSnapshot.getValue(DataElektronik::class.java)
                    if (elektronik?.userID == userID) {
                        elektronik?.let {
                            totalDaya = totalDaya + (it.daya*it.jmlUnit*it.durasi*(it.ulang*4))/1000
                            totalBiaya = totalDaya * 1352
                        }
                    }
                }

                // Format totalBiaya dengan pemisah ribuan dan desimal

                val strTotalBiaya = "Rp " + decimalFormat.format(totalBiaya) + "/bulan"

                // Format totalDaya dengan dua angka desimal dan pemisah ribuan
                val strTotalDaya = decimalFormat.format(totalDaya)
                binding.tvTotalBiaya.setText(strTotalBiaya)
                binding.vMyKonsumsiDaya.setText(strTotalDaya)

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("BerandaFragment", "Failed to read value.", databaseError.toException())
            }
        }
        databaseReference.addValueEventListener(valueEventListener)
    }

    private fun fetchWeatherData(){
        val apiService = RetrofitClient.retrofit.create(ApiService::class.java)

        val call = apiService.getWeatherData("pekanbaru","2a748269a8d726dfc7514a3391ec8e54","metric")

        call.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    suhu = responseBody.main.temp
                    kelembaban = responseBody.main.humidity

                    var sunrise = responseBody.sys.sunrise*1000L
                    var sunset = responseBody.sys.sunset*1000L
                    sunshine = calculateDaylightDuration(sunrise,sunset)

                    val weatherList: List<Weather> = responseBody.weather

                    var kondisiCuaca = weatherList[0].description

                    binding.tvDatetime.setText(getCurrentDate())
                    binding.tvCuaca.setText(kondisiCuaca)
                    binding.tvSuhuCuaca.setText(suhu.toString() + " °C")
                    binding.tvLamaMatahari.setText("Lama Sinar Matahari: " + decimalFormat.format(sunshine))
                    binding.tvKelembaban.setText("Kelembaban: "+kelembaban.toString())
                    Log.d("SUHU","onResponse: $suhu")
                    Log.d("MATAHARI","onResponse: $sunshine")
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    // Fungsi untuk menghitung lama matahari bersinar
    private fun calculateDaylightDuration(sunriseTimestamp: Long, sunsetTimestamp: Long): Double {
        val daylightDurationMillis = sunsetTimestamp - sunriseTimestamp
        val daylightDurationHours = daylightDurationMillis.toDouble() / (60 * 60 * 1000)

        // Konversi durasi dari milidetik ke format HH:mm:ss
//        val daylightDurationFormatted = SimpleDateFormat("HH:mm:ss")
//            .apply {
//                timeZone = TimeZone.getTimeZone("UTC")
//            }
//            .format(Date(daylightDurationMillis))

        return daylightDurationHours
    }

    private fun getCurrentDate(): String {
        val calendar: Calendar = Calendar.getInstance()
        val currentDate: Date = calendar.time

        // Format tanggal sesuai dengan "EEEE-dd-MM-yyyy" (hari-tanggal-bulan-tahun)
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    private fun prediksiDaya_wCuaca(curahHujan:Double,suhu:Double,sunshine:Double,kelembaban:Int){
        val apiService = RetrofitModel.retrofit.create(APIModel::class.java)

        val inputData = FeaturesModel(curahHujan,suhu,sunshine,kelembaban)
        val call = apiService.getPredict(inputData)

        call.enqueue(object : Callback<TargetModel> {
            override fun onResponse(call: Call<TargetModel>, response: Response<TargetModel>) {
                if (response.isSuccessful) {
                    var prediction = response.body()?.prediction
                    hasilPrediksi = prediction!!
                    Log.e("hasil",hasilPrediksi.toString())

                    var strPrediksi = decimalFormat.format(hasilPrediksi)
                    binding.vWKonsumsiDaya.setText(strPrediksi)

                    var persentase = (totalDaya/hasilPrediksi)*100
                    var strPersen = decimalFormat.format(persentase)
                    binding.vPKonsumsiDaya.setText(strPersen)

                } else { }
            }

            override fun onFailure(call: Call<TargetModel>, t: Throwable) {
                Log.e("hasil", "Error: ${t.message}")
                t.printStackTrace()
            }
        })
    }

    private fun tampilDetailPrediksi(){
        detailPrediksi = DetailPrediksiMlBinding.inflate(layoutInflater)

        detailPrediksi.etInSuhu.setText(suhu.toString())
        detailPrediksi.etInCurahHujan.setText(curahHujan.toString())
        detailPrediksi.etInSunshine.setText(sunshine.toString())
        detailPrediksi.etInKelembaban.setText(kelembaban.toString())

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(detailPrediksi.root)
            .setPositiveButton("Prediksi", null)  // null di sini untuk menahan dismiss
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

// Tampilkan dialog
        dialog.show()

// Ambil tombol positif dari dialog
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            // Lakukan operasi prediksi atau yang diperlukan di sini
            var rain = detailPrediksi.etInCurahHujan.text.toString().toDouble()
            var temp = detailPrediksi.etInSuhu.text.toString().toDouble()
            var light = detailPrediksi.etInSunshine.text.toString().toDouble()
            var humidity = detailPrediksi.etInKelembaban.text.toString().toInt()

            prediksiDaya_wCuaca(rain, temp, light, humidity)
            Log.e("hasilPrediksi di Dialog", hasilPrediksi.toString())
            detailPrediksi.etHasilPrediksi.setText(hasilPrediksi.toString())

            // Jangan lupa untuk memanggil dismiss jika ingin menutup dialog secara manual
            // dialog.dismiss()
        }
    }

    private fun initRecyclerView() {
        binding.rvCatatan.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL, false)
            rekomendasiAdapter = RecyclerAdapter_Rekomendasi()
            adapter = rekomendasiAdapter
        }
    }

    private fun getDataRekomendasi(){
        databaseReference = FirebaseDatabase.getInstance().getReference("rekomendasi")
        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listDataRekomendasi = mutableListOf<DataRekomendasi>()

                var index=0
                for (rekomendasiSnapshot in dataSnapshot.children) {
                    val rekomendasi = rekomendasiSnapshot.getValue(DataRekomendasi::class.java)

                    if (rekomendasi != null) {
                        rekomendasi.nomor = ++index
                        listDataRekomendasi.add(rekomendasi)
                    }
                }

                initRecyclerView()
                rekomendasiAdapter.submitList(listDataRekomendasi)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("Rekomendasi", "Failed to read value.", databaseError.toException())
            }
        }

        databaseReference.addValueEventListener(valueEventListener)
    }

    private fun getNamaUser(){
        auth = FirebaseAuth.getInstance()
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val userRef: DatabaseReference = database.getReference("users").child(auth.currentUser!!.uid)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Mendapatkan data dari dataSnapshot
                if (dataSnapshot.exists()) {
                    val nama = dataSnapshot.child("nama").getValue(String::class.java)

                    binding.tvHiUser.setText("Hi, " + nama + "!")
                } else {
                    // Data dengan ID tertentu tidak ditemukan
                    println("Data dengan ID $id tidak ditemukan.")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Penanganan kesalahan (jika ada)
                println("Gagal mengambil data: ${databaseError.message}")
            }
        })
    }
}