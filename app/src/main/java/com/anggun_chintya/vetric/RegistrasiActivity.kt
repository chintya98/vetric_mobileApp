package com.anggun_chintya.vetric

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anggun_chintya.vetric.databinding.ActivityRegistrasiBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistrasiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrasiBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrasiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("users")

        binding.btnTextLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnDaftar.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            // Registrasi akun dengan Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Akun berhasil terdaftar
                        val user = auth.currentUser
                        if (user != null) {
                            // Menambah data pengguna ke Realtime Database
                            val userId = user.uid
                            val userMap = mapOf(
                                "nama" to binding.etNama.text.toString().trim(),
                                "email" to email,
                                "jenis_layanan" to "null"
                            )

                            database.child(userId).setValue(userMap)
                                .addOnCompleteListener { databaseTask ->
                                    if (databaseTask.isSuccessful) {
                                        // Data pengguna berhasil ditambahkan
                                        Toast.makeText(
                                            this,
                                            "Registrasi berhasil",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        val intent = Intent(this, LoginActivity::class.java)
                                        intent.putExtra("fromRegis",true)
                                        startActivity(intent)
                                    } else {
                                        Log.e("RegisterActivity", "Gagal menambahkan data pengguna", databaseTask.exception)
                                        Toast.makeText(
                                            this,
                                            "Gagal registrasi",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        }
                    } else {
                        // Registrasi gagal
                        Log.e("RegisterActivity", "Gagal registrasi", task.exception)
                        Toast.makeText(this, "Gagal registrasi", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}