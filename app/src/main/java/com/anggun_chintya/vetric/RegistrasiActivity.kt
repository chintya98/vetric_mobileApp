package com.anggun_chintya.vetric

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anggun_chintya.vetric.databinding.ActivityRegistrasiBinding

class RegistrasiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrasiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrasiBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}