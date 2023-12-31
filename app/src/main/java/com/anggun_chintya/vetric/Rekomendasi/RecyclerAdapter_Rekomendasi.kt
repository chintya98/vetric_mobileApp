package com.anggun_chintya.vetric.Rekomendasi

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.anggun_chintya.vetric.Elektronik.DataElektronik
import com.anggun_chintya.vetric.Elektronik.EditElektronik
import com.anggun_chintya.vetric.databinding.ItemElektronikBinding
import com.anggun_chintya.vetric.databinding.ItemRekomendasiBinding
import com.google.android.material.card.MaterialCardView

class RecyclerAdapter_Rekomendasi : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items : List<DataRekomendasi> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RekomendasiViewHolder {
        val binding = ItemRekomendasiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RekomendasiViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is RekomendasiViewHolder -> {
                holder.bind(items.get(position))
            }
        }
    }

    fun submitList (listRekomendasi:List<DataRekomendasi>){
        items = listRekomendasi
    }

    class RekomendasiViewHolder constructor(val binding: ItemRekomendasiBinding):RecyclerView.ViewHolder(binding.root){
        val judul : TextView = binding.judul
        val desc : TextView = binding.desc

        fun bind (dataRekomendasi: DataRekomendasi){
            judul.text = dataRekomendasi.title
            desc.text = dataRekomendasi.desc
        }

    }
}