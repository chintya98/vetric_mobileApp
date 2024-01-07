package com.anggun_chintya.vetric.Catatan

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.anggun_chintya.vetric.MainActivity
import com.anggun_chintya.vetric.R
import com.anggun_chintya.vetric.databinding.EditCatatanBinding
import com.anggun_chintya.vetric.databinding.ItemCatatanBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.tan

class RecyclerAdapter_Catatan : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items : List<DataCatatan> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatatanViewHolder {
        val binding = ItemCatatanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CatatanViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is CatatanViewHolder -> {
                holder.bind(items.get(position))
                holder.card.setOnClickListener {
                    holder.click(items.get(position))
                }
            }
        }
    }

    fun submitList (listCatatan:List<DataCatatan>){
        items = listCatatan
    }

    class CatatanViewHolder constructor(val binding: ItemCatatanBinding):RecyclerView.ViewHolder(binding.root){
        private lateinit var customView : EditCatatanBinding

        val judul : TextView = binding.tvJudulCat
        val biaya : TextView = binding.tvBiayaPengeluaran
        val bulan : TextView = binding.tvBulan
        val tanggal : TextView = binding.tvTgl

        var card : MaterialCardView = binding.itemCatatan

        fun bind (dataCatatan: DataCatatan){
            val decimalFormat = DecimalFormat("#,##0.00")

            judul.text = dataCatatan.judul

            var str_pengeluaran = decimalFormat.format(dataCatatan.pengeluaran)
            biaya.text = "Rp " + str_pengeluaran

            val monthFormat = SimpleDateFormat("MMM", Locale.US)
            val formattedMonth = monthFormat.format(dataCatatan.tanggal)

            val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
            val formattedDate = dateFormat.format(dataCatatan.tanggal)

            bulan.text = formattedMonth.toString()
            tanggal.text = formattedDate.toString()
        }

        fun click(get: DataCatatan){
            Toast.makeText(itemView.context,"kamu memilih : ${get.judul}",Toast.LENGTH_SHORT).show()

//            var customView: EditCatatanBinding
            customView = EditCatatanBinding.inflate(LayoutInflater.from(itemView.context))

            val dialog = MaterialAlertDialogBuilder(itemView.context)
                .setView(customView.root)
                .setTitle("Edit Catatan")

            var judul = get.judul
            var biaya = get.pengeluaran.toString()

            val inputDateFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZ yyyy", Locale.US)
            val outputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

            var tanggal = get.tanggal.toString()
            val date = inputDateFormat.parse(tanggal)
            val formattedDate = outputDateFormat.format(date)

            customView.etEdJudul.setText(judul)
            customView.etEdBiaya.setText(biaya)
            customView.etEdTgl.setText(formattedDate)

//            Log.e("dapat nilai",judul+" ${biaya} ${formattedDate}")

            dialog.setPositiveButton("Simpan") { dialog, _ ->
                    updateData(get.id)
                    dialog.dismiss()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            // Membuat objek AlertDialog dari MaterialAlertDialogBuilder
            val dialogUtama: androidx.appcompat.app.AlertDialog = dialog.create()

            customView.etEdTgl.setOnClickListener {
                tampilkanDatePicker()
            }

            customView.hapusNote.setOnClickListener {
                hapusData(get.id,dialogUtama)
            }

            dialogUtama.show()
        }

        private fun hapusData(id: String, dialog1: androidx.appcompat.app.AlertDialog){
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val catatanRef: DatabaseReference = database.getReference("catatan").child(id)

            MaterialAlertDialogBuilder(itemView.context)
                .setTitle("Hapus Catatan Ini?")
                .setMessage("Data akan terhapus dari perangkat Anda")

                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Hapus") { dialog, which ->
                    catatanRef.removeValue()
                        .addOnSuccessListener {

                            Toast.makeText(itemView.context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                            dialog1.dismiss()
                        }
                        .addOnFailureListener {
                            Toast.makeText(itemView.context, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
                        }
                }
                .show()
        }

        private fun updateData(id : String){
            val database: FirebaseDatabase = FirebaseDatabase.getInstance()
            val catatanRef: DatabaseReference = database.getReference("catatan").child(id)


            var judul = customView.etEdJudul.text.toString()
            var biaya = customView.etEdBiaya.text.toString().toDouble()

            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

            var tanggal = customView.etEdTgl.text.toString()
            val date = dateFormat.parse(tanggal)
            Log.e("date",date.toString())

            val updateData = mapOf<String, Any>(
                "judul" to judul,
                "pengeluaran" to biaya,
                "tanggal" to date,
            )

            // Memanggil metode updateChildren untuk melakukan pembaruan
            catatanRef.updateChildren(updateData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(itemView.context, "Perubahan berhasil disimpan", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(itemView.context, "Gagal menyimpan perubahan", Toast.LENGTH_SHORT).show()
                    }
                }

        }

        private fun tampilkanDatePicker() {
            val calendar = Calendar.getInstance()

            val datePickerDialog = DatePickerDialog(
                itemView.context,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)

                    // Format tanggal yang dipilih dan atur ke dalam editTextTanggal
                    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)
                    customView.etEdTgl.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.show()
        }
    }
}