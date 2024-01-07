package com.anggun_chintya.vetric

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.anggun_chintya.vetric.Elektronik.DataTipeElektronik
import com.anggun_chintya.vetric.databinding.FragmentBerandaBinding
import com.anggun_chintya.vetric.databinding.FragmentElektronikBinding
import com.anggun_chintya.vetric.databinding.FragmentProfilBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProfilFragment : Fragment() {

    private lateinit var binding : FragmentProfilBinding
    private lateinit var auth: FirebaseAuth

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val userRef: DatabaseReference = database.getReference("users")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfilBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()

        binding.txEmail.setText(auth.currentUser!!.email)
        tampilProfil(auth.currentUser!!.uid)

        binding.txJenisLyn.setOnClickListener {
            tampilkanDialogLayanan()
        }


        binding.btnLogout.setOnClickListener {
            signOut()
        }
        return binding.root
    }

    private fun signOut() {
        auth.signOut()

        val intent = Intent(requireActivity(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    private fun tampilProfil(id:String){
        val profilRef = userRef.child(id)

        profilRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Mendapatkan data dari dataSnapshot
                if (dataSnapshot.exists()) {
                    val nama = dataSnapshot.child("nama").getValue(String::class.java)
                    val layanan = dataSnapshot.child("jenis_layanan").getValue(String::class.java)

                    binding.txUsername.setText(nama)
                    if(layanan=="null"){
                        binding.txJenisLyn.setText("Pilih Jenis Layanan Listrik Anda!")
                    }else{
                        binding.txJenisLyn.setText(layanan)
                    }
                    println("Nama: $nama, Email: $layanan")
                } else {
                    // Data dengan ID tertentu tidak ditemukan
                    println("Data dengan ID $id tidak ditemukan.")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Penanganan kesalahan (jika ada)
                println("Gagal mengambil data: ${databaseError.message}")
            }
        })
    }

    private fun editLayanan(id: String){
        val profilRef = userRef.child(id)

        val layanan = binding.txJenisLyn.text.toString()
        val updateData = HashMap<String, Any>()
        updateData["jenis_layanan"] = layanan

        profilRef.updateChildren(updateData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    Toast.makeText(context, "Perubahan berhasil disimpan", Toast.LENGTH_SHORT).show()

                } else {
                    Toast.makeText(context, "Gagal menyimpan perubahan", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun tampilkanDialogLayanan() {
        val layananOptions = arrayOf("Prabayar", "Pascabayar")
        val selectedLayanan = binding.txJenisLyn.text.toString()

        val dialogBuilder = MaterialAlertDialogBuilder(requireActivity())
        dialogBuilder.setTitle("Jenis Layanan Listrik")
            .setSingleChoiceItems(layananOptions, if (selectedLayanan == "Prabayar" || selectedLayanan == "Pascabayar") layananOptions.indexOf(selectedLayanan) else -1) { dialog, which ->
                // Tindakan yang akan diambil saat salah satu item dipilih
                val selectedLayanan = layananOptions[which]
                binding.txJenisLyn.setText(selectedLayanan)

                // Lakukan sesuatu dengan nilai yang dipilih (misalnya, tampilkan toast)
                Toast.makeText(requireContext(), "Anda memilih: $selectedLayanan", Toast.LENGTH_SHORT).show()

                // Tutup dialog setelah pengguna memilih
                dialog.dismiss()
                editLayanan(auth.currentUser!!.uid)
            }
            .setNegativeButton("Batal") { dialog, _ ->
                // Tindakan yang akan diambil saat tombol Batal diklik
                // Misalnya, tutup dialog
                dialog.dismiss()
            }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

}