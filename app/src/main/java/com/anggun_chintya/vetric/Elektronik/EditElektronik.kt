package com.anggun_chintya.vetric.Elektronik

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.anggun_chintya.vetric.MainActivity
import com.anggun_chintya.vetric.R
import com.anggun_chintya.vetric.databinding.EditElektronikBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.DecimalFormat

class EditElektronik : AppCompatActivity() {

    private lateinit var binding: EditElektronikBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditElektronikBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra("id")){
            val id: String = this.intent.getStringExtra("id").toString()
            val kategori: String = this.intent.getStringExtra("kategori").toString()
            val nama: String = this.intent.getStringExtra("nama").toString()
            val tipe: String = this.intent.getStringExtra("tipe").toString()
            val daya: String = this.intent.getStringExtra("daya").toString()
            val jmlUnit : String = this.intent.getStringExtra("jmlUnit").toString()
            val durasi : String = this.intent.getStringExtra("durasi").toString()
            val ulang : String = this.intent.getStringExtra("ulang").toString()

            tampilDetil(kategori,nama,tipe, daya, jmlUnit, durasi, ulang)

            binding.btnEdit.setOnClickListener {
                updateData(id)
            }

            binding.hapusElec.setOnClickListener {
                hapusData(id)
            }
        }

        binding.etEdKategori.setOnClickListener {
            tampilkanDialogKategori()
        }

        binding.etEdTipe.setOnClickListener {
            tipeKategori()
        }

    }

    private fun getKategoriImageResId(kategori: String): Int {
        return when (kategori) {
            "Pendingin Udara" -> R.drawable.ac
            "Kulkas" -> R.drawable.kulkas
            "Televisi" -> R.drawable.tv
            "Komputer" -> R.drawable.computer
            "Penanak Nasi" -> R.drawable.penanaknasi
            "Lampu" -> R.drawable.lampu
            else -> R.drawable.ic_elec // Gambar default jika kategori tidak dikenali
        }
    }

    private fun tampilDetil(kategori:String, nama:String, tipe:String, daya:String,
                            jmlUnit:String, durasi:String, ulang:String){

        val decimalFormat = DecimalFormat("#,##0.00")

        binding.etEdKategori.setText(kategori)
        binding.etEdNama.setText(nama)
        binding.etEdTipe.setText(tipe)
        binding.etEdDaya.setText(daya)
        binding.etEdUnit.setText(jmlUnit)
        binding.etEdDurasi.setText(durasi)
        binding.etEdUlang.setText(ulang)

        val biaya = (durasi.toDouble() * jmlUnit.toInt() * daya.toDouble() * ulang.toInt()*4)/1000 * 1352
        binding.etBiaya.setText(decimalFormat.format(biaya))

        val daya = (durasi.toDouble() * jmlUnit.toInt() * daya.toDouble() * ulang.toInt()*4)/1000
        binding.etDaya.setText(decimalFormat.format(daya))

        val kategoriImageResId = getKategoriImageResId(kategori)
        binding.icElec.setImageResource(kategoriImageResId)
    }

    private fun updateData(id: String) {
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val elektronikRef: DatabaseReference = database.getReference("elektronik").child(id)

        val kategori = binding.etEdKategori.text.toString()
        val nama = binding.etEdNama.text.toString()
        val tipe = binding.etEdTipe.text.toString()
        val daya = binding.etEdDaya.text.toString()
        val jmlUnit = binding.etEdUnit.text.toString()
        val durasi = binding.etEdDurasi.text.toString()
        val ulang = binding.etEdUlang.text.toString()

        // Membuat map untuk menyimpan nilai-nilai yang ingin diperbarui
        val updateData = mapOf<String, Any>(
            "kategori" to kategori,
            "nama" to nama,
            "tipe" to tipe,
            "daya" to daya.toDouble(),
            "jmlUnit" to jmlUnit.toInt(),
            "durasi" to durasi.toDouble(),
            "ulang" to ulang.toInt()
        )

        // Memanggil metode updateChildren untuk melakukan pembaruan
        elektronikRef.updateChildren(updateData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("openFragment", "ElektronikFragment") // Anda dapat mengirimkan data lain jika diperlukan
                    startActivity(intent)
                    finish()

                    Toast.makeText(this, "Perubahan berhasil disimpan", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(this, "Gagal menyimpan perubahan", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun hapusData(id: String){
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val elektronikRef: DatabaseReference = database.getReference("elektronik").child(id)

        MaterialAlertDialogBuilder(this)
            .setTitle("Hapus Elektronik Ini?")
            .setMessage("Data akan terhapus dari perangkat Anda")

            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Hapus") { dialog, which ->
                elektronikRef.removeValue()
                    .addOnSuccessListener {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("openFragment", "ElektronikFragment") // Anda dapat mengirimkan data lain jika diperlukan
                        startActivity(intent)
                        finish()

                        Toast.makeText(this, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
                    }
            }
            .show()
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
                val lastSelectedCategory = binding.etEdKategori.text.toString() // Ganti dengan key yang sesuai
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
                    binding.etEdKategori.setText(selectedCategory.kategori)

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
        if(binding.etEdKategori.text.toString()!=null){
            var kat = binding.etEdKategori.text.toString()
            if(kat =="Pendingin Udara" || kat=="Kipas Angin" || kat=="Lampu"
                || kat=="Printer" || kat=="Kulkas" || kat=="Televisi" || kat=="Mesin Cuci" || kat=="Dispenser"){
                tampilkanDialogTipeKategori()
            }else{
                binding.etEdTipe.setText("-")
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
                    if(tipe?.kategori == binding.etEdKategori.text.toString()){
                        tipe?.let {
                            tipeList.add(it)
                        }
                    }
                }

                // Buat array yang berisi nama-nama kategori
                val tipeNames = tipeList.map { it.tipe ?: "" }.toTypedArray()

                // Tentukan indeks kategori yang dipilih (default: 0)
                var selectedCategoryIndex = 0

                val lastSelectedType = binding.etEdTipe.text.toString() // Ganti dengan key yang sesuai
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
                    binding.etEdTipe.setText(selectedCategory.tipe)

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