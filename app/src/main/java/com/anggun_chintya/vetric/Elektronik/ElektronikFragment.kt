package com.anggun_chintya.vetric.Elektronik

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.anggun_chintya.vetric.databinding.FragmentElektronikBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ElektronikFragment : Fragment() {

    private lateinit var elektronikAdapter: RecyclerAdapter_Elektronik
    private lateinit var binding: FragmentElektronikBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentElektronikBinding.inflate(layoutInflater)

        databaseReference = FirebaseDatabase.getInstance().getReference("elektronik")
        getDataElektronik()

        binding.btnAddElektronik.setOnClickListener {
            val intent = Intent(activity, TambahElektronik::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    private fun initRecyclerView() {
        binding.rvElektronik.apply {
            layoutManager = LinearLayoutManager(requireContext())
            elektronikAdapter = RecyclerAdapter_Elektronik()
            adapter = elektronikAdapter
        }
    }

    private fun getDataElektronik() {

        val auth= FirebaseAuth.getInstance()
        var userID = auth.currentUser?.uid

        val valueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val listDataElektronik = mutableListOf<DataElektronik>()

                for (elektronikSnapshot in dataSnapshot.children) {
                    val elektronik = elektronikSnapshot.getValue(DataElektronik::class.java)
                    if (elektronik?.userID == userID) {
                        elektronik?.let {
                            listDataElektronik.add(it)
                        }
                    }
                }

                if (listDataElektronik.isEmpty()) {
                    // Tampilkan pesan bahwa tidak ada data
                    binding.noData.visibility = View.VISIBLE
                    Log.e("h1","tidak ada data")
                } else {
                    // Sembunyikan pesan jika ada data
                    binding.noData.visibility = View.GONE
                    Log.e("h2","ada data")
                }

                initRecyclerView()
                elektronikAdapter.submitList(listDataElektronik)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("ElektronikFragment", "Failed to read value.", databaseError.toException())
            }
        }

        databaseReference.addValueEventListener(valueEventListener)
    }
}
