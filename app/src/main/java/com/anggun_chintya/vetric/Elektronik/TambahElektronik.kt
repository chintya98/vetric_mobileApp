package com.anggun_chintya.vetric.Elektronik

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
            val kategori = binding.etInKategori.text.toString().trim()
            val nama = binding.etInNama.text.toString().trim()
            val tipe = binding.etInTipe.text.toString().trim()
            val dayaStr = binding.etInDaya.text.toString().trim()
            val jmlUnitStr = binding.etInUnit.text.toString().trim()
            val durasiStr = binding.etInDurasi.text.toString().trim()
            val ulangStr = binding.etInUlang.text.toString().trim()

            if (kategori.isEmpty() || nama.isEmpty() || tipe.isEmpty() || dayaStr.isEmpty() || jmlUnitStr.isEmpty() || durasiStr.isEmpty() || ulangStr.isEmpty()) {
                // Tampilkan AlertDialog jika ada input yang kosong
                tampilkanAlertDialog("Inputan tidak boleh kosong.")
            } else if(ulangStr.toInt()<1 || ulangStr.toInt()>7) {
                tampilkanAlertDialog("Inputkan jumlah pemakaian yang valid dalam 1 minggu (nilai 1 s/d 7)")
                Log.e("disinierror",ulangStr)
            }else {
                try {
                    val progressBar = ProgressDialog(this)
                    progressBar.setMessage("Menambah Data...")
                    progressBar.show()

                    val daya = dayaStr.toDouble()
                    val jmlUnit = jmlUnitStr.toInt()
                    val durasi = durasiStr.toDouble()
                    val ulang = ulangStr.toInt()

                    // Panggil fungsi simpanDataElektronik jika input valid
                    simpanDataElektronik(kategori, nama, tipe, daya, jmlUnit, durasi, ulang)
                    progressBar.dismiss()
                } catch (e: NumberFormatException) {
                    // Tampilkan AlertDialog jika input tidak valid
                    tampilkanAlertDialog("Format input tidak valid.")
                }
            }
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

        // Menyimpan data ke Firebase
        elektronikRef.child(newId ?: "").setValue(dataElektronik)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
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

                // Cek apakah ada kategori yang terakhir dipilih (misalnya dari SharedPreferences)
                val lastSelectedCategory = binding.etInKategori.text.toString() // Ganti dengan key yang sesuai
                val lastSelectedIndex = kategoriList.indexOfFirst { it.kategori == lastSelectedCategory }

                if (lastSelectedIndex != -1) {
                    // Kategori terakhir ditemukan, set selectedCategoryIndex ke indeks tersebut
                    selectedCategoryIndex = lastSelectedIndex
                }

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


                val lastSelectedType = binding.etInTipe.text.toString() // Ganti dengan key yang sesuai
                val lastSelectedIndex = tipeList.indexOfFirst { it.tipe == lastSelectedType }

                if (lastSelectedIndex != -1) {
                    selectedCategoryIndex = lastSelectedIndex
                }

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

    private fun tampilkanAlertDialog(message: String) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Peringatan")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .create()

        alertDialog.show()
    }
}