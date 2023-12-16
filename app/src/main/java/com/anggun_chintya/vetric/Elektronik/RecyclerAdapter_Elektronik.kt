package com.anggun_chintya.vetric.Elektronik

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anggun_chintya.vetric.databinding.ItemElektronikBinding

class RecyclerAdapter_Elektronik : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items : List<DataElektronik> = ArrayList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElektronikViewHolder {
        val binding = ItemElektronikBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ElektronikViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ElektronikViewHolder -> {
                holder.bind(items.get(position))
            }
        }
    }

    fun submitList (listElektronik:List<DataElektronik>){
        items = listElektronik
    }

    class ElektronikViewHolder constructor(val binding: ItemElektronikBinding):RecyclerView.ViewHolder(binding.root){
        val icon : ImageView = binding.ivIcon
        val kategori : TextView = binding.tvKategori
        val tipe : TextView = binding.tvTipe
        val jmlUnit : TextView = binding.tvJmlUnit
        val daya : TextView = binding.tvDaya
        val estBiaya : TextView = binding.tvEstBiaya

        fun bind (dataElektronik: DataElektronik){
            kategori.text = dataElektronik.kategori
            tipe.text = dataElektronik.tipe
            jmlUnit.text = dataElektronik.jmlUnit.toString()
            daya.text = dataElektronik.daya.toString()

            val biaya = dataElektronik.durasi * dataElektronik.jmlUnit * dataElektronik.daya * 1352
            estBiaya.text = biaya.toString()
        }
    }
}