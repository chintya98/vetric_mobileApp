package com.anggun_chintya.vetric

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anggun_chintya.vetric.databinding.TambahElektronikBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class TambahElektronik : AppCompatActivity(){

    private lateinit var binding : TambahElektronikBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TambahElektronikBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSaveElektronik.setOnClickListener {
            // Mendapatkan nilai dari EditText
            val kategori = binding.etInKategori.text.toString()
            val nama = binding.etInNama.text.toString()
            val tipe = binding.etInTipe.text.toString()
            val daya = binding.etInDaya.text.toString().toDouble()
            val jmlUnit = binding.etInUnit.text.toString().toInt()
            val durasi = binding.etInDurasi.text.toString().toDouble()
            val ulang = binding.etInUlang.text.toString().toInt()

            simpanDataElektronik(kategori, nama, tipe, daya,jmlUnit, durasi, ulang)
        }
    }

    fun simpanDataElektronik(kategori:String, nama:String, tipe:String, daya:Double, jmlUnit:Int, durasi:Double, ulang:Int) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference: DatabaseReference = database.reference

        val dataElektronik = DataElektronik(kategori,nama,tipe,daya,jmlUnit,durasi,ulang)
        val elektronikRef = databaseReference.child("elektronik")

        val progressbar = binding.progressbar
        progressbar.visibility = View.VISIBLE

        // Menyimpan data ke Firebase
        elektronikRef.push().setValue(dataElektronik)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressbar.visibility = View.INVISIBLE
                    tampilkanFragmentElektronik()
                } else {
                    Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun tampilkanFragmentElektronik() {
        val fragmentManager = supportFragmentManager
        val elektronikFragment = ElektronikFragment()

        // Ganti fragment yang sedang ditampilkan dengan ElektronikFragment
        fragmentManager?.beginTransaction()?.replace(R.id.frame_layout, elektronikFragment)?.commit()
    }
}