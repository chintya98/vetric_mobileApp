package com.anggun_chintya.vetric

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.anggun_chintya.vetric.databinding.FragmentElektronikBinding
import com.anggun_chintya.vetric.databinding.FragmentProfilBinding
import com.google.firebase.auth.FirebaseAuth


class ProfilFragment : Fragment() {

    private lateinit var binding : FragmentProfilBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfilBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()

        binding.txEmail.setText(auth.currentUser?.email)


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
}