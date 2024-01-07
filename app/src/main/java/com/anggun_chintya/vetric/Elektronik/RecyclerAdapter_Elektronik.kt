package com.anggun_chintya.vetric.Elektronik

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.anggun_chintya.vetric.R
import com.anggun_chintya.vetric.databinding.ItemElektronikBinding
import com.google.android.material.card.MaterialCardView
import java.text.DecimalFormat

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
                holder.card.setOnClickListener {
                    holder.click(items.get(position))
                }
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

        var card : MaterialCardView = binding.itemElec

        fun bind (dataElektronik: DataElektronik){
            val decimalFormat = DecimalFormat("#,##0.00")

            kategori.text = dataElektronik.kategori
            tipe.text = dataElektronik.tipe
            jmlUnit.text = dataElektronik.jmlUnit.toString()

            val num_daya = (dataElektronik.durasi * dataElektronik.jmlUnit * dataElektronik.daya *dataElektronik.ulang*4)/1000
            daya.text = decimalFormat.format(num_daya)+ " kWh/bulan"

            val biaya = (dataElektronik.durasi * dataElektronik.jmlUnit * dataElektronik.daya *dataElektronik.ulang*4)/1000 * 1352
            estBiaya.text = decimalFormat.format(biaya)

            val kategoriImageResId = getKategoriImageResId(dataElektronik.kategori)
            icon.setImageResource(kategoriImageResId)
        }

        fun click(get: DataElektronik){
            Toast.makeText(itemView.context,"kamu memilih : ${get.nama}",Toast.LENGTH_SHORT).show()

            val intent = Intent(itemView.context, EditElektronik::class.java)
            intent.putExtra("id",get.id)
            intent.putExtra("kategori",get.kategori)
            intent.putExtra("nama",get.nama)
            intent.putExtra("tipe",get.tipe)
            intent.putExtra("daya",get.daya.toString())
            intent.putExtra("jmlUnit",get.jmlUnit.toString())
            intent.putExtra("durasi",get.durasi.toString())
            intent.putExtra("ulang",get.ulang.toString())
            itemView.context.startActivity(intent)
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
    }
}