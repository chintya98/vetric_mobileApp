package com.anggun_chintya.vetric.Catatan

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.anggun_chintya.vetric.R
import com.anggun_chintya.vetric.databinding.FragmentCatatanBinding
import com.anggun_chintya.vetric.databinding.TambahCatatanBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CatatanFragment : Fragment() {

    private lateinit var binding: FragmentCatatanBinding
    private lateinit var customView : TambahCatatanBinding
    private lateinit var catatanAdapter : RecyclerAdapter_Catatan
    private lateinit var databaseReference: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCatatanBinding.inflate(layoutInflater)

        databaseReference = FirebaseDatabase.getInstance().getReference("catatan")
        getDataCatatan()

        binding.btnAddCatatan.setOnClickListener {
            formTambah()
        }

        return binding.root
    }

    private fun formTambah(){
        customView = TambahCatatanBinding.inflate(layoutInflater)
        customView.etInTgl.setOnClickListener {
            tampilkanDatePicker()
        }

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(customView.root)
            .setPositiveButton("Simpan") { dialog, _ ->
                var judul = customView.etInJudul.text.toString()
                var biaya = customView.etInBiaya.text.toString().toDouble()

                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

                var tanggal = customView.etInTgl.text.toString()
                val date = dateFormat.parse(tanggal)
                Log.e("date",date.toString())

                simpanDataCatatan(judul, biaya, date)
            }
            .setNegativeButton("Batal") { dialog, _ ->

                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun initRecyclerView() {
        binding.rvCatatan.apply {
            layoutManager = LinearLayoutManager(requireContext())
            catatanAdapter = RecyclerAdapter_Catatan()
            adapter = catatanAdapter
        }
    }

    private fun getDataCatatan() {

        val auth= FirebaseAuth.getInstance()
        var userID = auth.currentUser?.uid

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listDataCatatan = mutableListOf<DataCatatan>()

                for (elektronikSnapshot in dataSnapshot.children) {
                    val catatan = elektronikSnapshot.getValue(DataCatatan::class.java)
                    if (catatan?.userID == userID) {
                        catatan?.let {
                            listDataCatatan.add(it)
                        }
                    }
                }

                if (listDataCatatan.isEmpty()) {
                    // Tampilkan pesan bahwa tidak ada data
                    binding.noData.visibility = View.VISIBLE
                    Log.e("h1","tidak ada data")
                } else {
                    // Sembunyikan pesan jika ada data
                    binding.noData.visibility = View.GONE
                    Log.e("h2","ada data")
                }

                initRecyclerView()
                catatanAdapter.submitList(listDataCatatan)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("ElektronikFragment", "Failed to read value.", databaseError.toException())
            }
        }

        databaseReference.addValueEventListener(valueEventListener)
    }

    private fun simpanDataCatatan(judul:String, biaya:Double, date:Date){
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val databaseReference: DatabaseReference = database.reference

        val auth= FirebaseAuth.getInstance()
        var userID = auth.currentUser?.uid

        val newId = databaseReference.child("catatan").push().key

        val dataCatatan =
            newId?.let { DataCatatan(it,judul,date,biaya,userID) }
        val catatanRef = databaseReference.child("catatan")


        // Menyimpan data ke Firebase
        catatanRef.child(newId ?: "").setValue(dataCatatan)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val fragmentManager = requireActivity().supportFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()

                    val catatanFragment = CatatanFragment()
                    fragmentTransaction.replace(R.id.frame_layout, catatanFragment)
                    fragmentTransaction.commit()
                } else {
                    Toast.makeText(requireContext(), "Gagal menyimpan data", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun tampilkanDatePicker() {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                // Format tanggal yang dipilih dan atur ke dalam editTextTanggal
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                customView.etInTgl.setText(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }
}