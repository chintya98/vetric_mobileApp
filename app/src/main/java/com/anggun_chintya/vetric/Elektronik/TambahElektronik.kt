package com.anggun_chintya.vetric.Elektronik

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anggun_chintya.vetric.MainActivity
import com.anggun_chintya.vetric.R
import com.anggun_chintya.vetric.databinding.TambahElektronikBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TambahElektronik : AppCompatActivity(){

    private lateinit var binding : TambahElektronikBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TambahElektronikBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvTambah.setOnClickListener {
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

        binding.etInKategori.setOnClickListener {
            tampilkanDialogKategori()
        }

        binding.etInTipe.setOnClickListener {
            tipeKategori()
        }

        binding.ibBack.setOnClickListener {
            onBackPressed()
        }
    }

    fun simpanDataElektronik(kategori:String, nama:String, tipe:String, daya:Double, jmlUnit:Int, durasi:Double, ulang:Int) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference: DatabaseReference = database.reference

        val auth= FirebaseAuth.getInstance()
        var userID = auth.currentUser?.uid

        val newId = databaseReference.child("elektronik").push().key

        val dataElektronik =
            newId?.let { DataElektronik(it,kategori,nama,tipe,daya,jmlUnit,durasi,ulang,userID) }
        val elektronikRef = databaseReference.child("elektronik")

        val progressbar = binding.progressbar
        progressbar.visibility = View.VISIBLE

        // Menyimpan data ke Firebase
        elektronikRef.child(newId ?: "").setValue(dataElektronik)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressbar.visibility = View.INVISIBLE
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("openFragment", "ElektronikFragment") // Anda dapat mengirimkan data lain jika diperlukan
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun tampilkanDialogKategori() {
        val dialogBuilder = MaterialAlertDialogBuilder(this)
        dialogBuilder.setTitle("Kategori")

        // Ambil daftar kategori dari Firebase Realtime Database
        val kategoriReference = FirebaseDatabase.getInstance().getReference("kategori_elektronik")
        kategoriReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val kategoriList = mutableListOf<DataKategoriElektronik>()

                for (kategoriSnapshot in dataSnapshot.children) {
                    val kategori = kategoriSnapshot.getValue(DataKategoriElektronik::class.java)
                    kategori?.let {
                        kategoriList.add(it)
                    }
                }

                // Buat array yang berisi nama-nama kategori
                val kategoriNames = kategoriList.map { it.kategori ?: "" }.toTypedArray()

                // Tentukan indeks kategori yang dipilih (default: 0)
                var selectedCategoryIndex = 0

                // Set up radio button dengan pilihan kategori
                dialogBuilder.setSingleChoiceItems(
                    kategoriNames,
                    selectedCategoryIndex
                ) { _, which ->
                    selectedCategoryIndex = which
                }

                // Atur tombol OK
                dialogBuilder.setPositiveButton("OK") { dialog, _ ->
                    // Ambil kategori yang dipilih
                    val selectedCategory = kategoriList[selectedCategoryIndex]

                    // Setel text field kategori dengan kategori yang dipilih
                    binding.etInKategori.setText(selectedCategory.kategori)

                    // Tutup dialog
                    dialog.dismiss()
                }

                // Atur tombol Batal
                dialogBuilder.setNegativeButton("Batal") { dialog, _ ->
                    // Tutup dialog tanpa melakukan apa-apa
                    dialog.dismiss()
                }

                // Tampilkan dialog
                dialogBuilder.create().show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle kegagalan membaca data
                // ...
            }
        })
    }

    private fun tipeKategori(){
        if(binding.etInKategori.text.toString()!=null){
            var kat = binding.etInKategori.text.toString()
            if(kat =="Pendingin Udara" || kat=="Kipas Angin" || kat=="Lampu"
                || kat=="Printer" || kat=="Kulkas" || kat=="Televisi" || kat=="Mesin Cuci" || kat=="Dispenser"){
                tampilkanDialogTipeKategori()
            }else{
                binding.etInTipe.setText("-")
            }
        }
    }

    private fun tampilkanDialogTipeKategori(){
        val dialogBuilder = MaterialAlertDialogBuilder(this)
        dialogBuilder.setTitle("Tipe")

        // Ambil daftar kategori dari Firebase Realtime Database
        val tipeReference = FirebaseDatabase.getInstance().getReference("tipe_elektronik")
        tipeReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tipeList = mutableListOf<DataTipeElektronik>()

                for (tipeSnapshot in dataSnapshot.children) {
                    val tipe = tipeSnapshot.getValue(DataTipeElektronik::class.java)
                    if(tipe?.kategori == binding.etInKategori.text.toString()){
                        tipe?.let {
                            tipeList.add(it)
                        }
                    }
                }

                // Buat array yang berisi nama-nama kategori
                val tipeNames = tipeList.map { it.tipe ?: "" }.toTypedArray()

                // Tentukan indeks kategori yang dipilih (default: 0)
                var selectedCategoryIndex = 0

                // Set up radio button dengan pilihan kategori
                dialogBuilder.setSingleChoiceItems(
                    tipeNames,
                    selectedCategoryIndex
                ) { _, which ->
                    selectedCategoryIndex = which
                }

                // Atur tombol OK
                dialogBuilder.setPositiveButton("OK") { dialog, _ ->
                    // Ambil kategori yang dipilih
                    val selectedCategory = tipeList[selectedCategoryIndex]

                    // Setel text field kategori dengan kategori yang dipilih
                    binding.etInTipe.setText(selectedCategory.tipe)

                    // Tutup dialog
                    dialog.dismiss()
                }

                // Atur tombol Batal
                dialogBuilder.setNegativeButton("Batal") { dialog, _ ->
                    // Tutup dialog tanpa melakukan apa-apa
                    dialog.dismiss()
                }

                // Tampilkan dialog
                dialogBuilder.create().show()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle kegagalan membaca data
                // ...
            }
        })
    }
}