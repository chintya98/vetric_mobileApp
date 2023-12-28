package com.anggun_chintya.vetric.Catatan

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.anggun_chintya.vetric.databinding.EditCatatanBinding
import com.anggun_chintya.vetric.databinding.ItemCatatanBinding
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
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
        val judul : TextView = binding.tvJudulCat
        val biaya : TextView = binding.tvBiayaPengeluaran
        val bulan : TextView = binding.tvBulan
        val tanggal : TextView = binding.tvTgl

        var card : MaterialCardView = binding.itemCatatan

        fun bind (dataCatatan: DataCatatan){
            judul.text = dataCatatan.judul
            biaya.text = dataCatatan.pengeluaran.toString()

            val monthFormat = SimpleDateFormat("MMM", Locale.US)
            val formattedMonth = monthFormat.format(dataCatatan.tanggal)

            val dateFormat = SimpleDateFormat("dd", Locale.getDefault())
            val formattedDate = dateFormat.format(dataCatatan.tanggal)

            bulan.text = formattedMonth.toString()
            tanggal.text = formattedDate.toString()

        }

        fun click(get: DataCatatan){
            Toast.makeText(itemView.context,"kamu memilih : ${get.judul}",Toast.LENGTH_SHORT).show()

            var customView: EditCatatanBinding
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

            Log.e("dapat nilai",judul+" ${biaya} ${formattedDate}")

            dialog.setPositiveButton("Simpan") { dialog, _ ->

                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            dialog.show()
        }
    }
}