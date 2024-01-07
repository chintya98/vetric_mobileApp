package com.anggun_chintya.vetric
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anggun_chintya.vetric.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        binding.btnTextDaftar.setOnClickListener {
            val intent = Intent(this, RegistrasiActivity::class.java)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser!=null) {
            // Pengguna sudah login, arahkan ke MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            setContentView(binding.root)

            binding.btnMasuk.setOnClickListener{
                val progressBar = ProgressDialog(this)
                progressBar.setMessage("Harap Menunggu...")
                progressBar.show()

                val email = binding.etLoginEmail.text.toString().trim()
                val password = binding.etLoginPassword.text.toString()

                if (email.isEmpty() || password.isEmpty() ) {
                    tampilkanAlertDialog("Inputan tidak boleh kosong.")
                }else{
                    try{
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    Log.e("disini",user?.email.toString())
                                    if (user != null) {
                                        // Start the MainActivity
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        // Login gagal
                                        Toast.makeText(this, "Login gagal", Toast.LENGTH_SHORT).show()
                                        Log.e("erroor","error login")
                                    }
                                }
                            }
                    }catch (e: NumberFormatException) {
                        // Tampilkan toast jika input biaya tidak valid
                        Toast.makeText(this, "Input biaya tidak valid", Toast.LENGTH_SHORT).show()
                    }
                }
                progressBar.dismiss()
            }
        }
    }

    private fun tampilkanAlertDialog(message: String) {
        val alertDialog = AlertDialog.Builder(this)
            .setTitle("Peringatan!")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .create()

        alertDialog.show()
    }
}