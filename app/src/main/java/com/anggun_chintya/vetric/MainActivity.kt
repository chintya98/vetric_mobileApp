package com.anggun_chintya.vetric

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.anggun_chintya.vetric.Catatan.CatatanFragment
import com.anggun_chintya.vetric.Elektronik.ElektronikFragment
import com.anggun_chintya.vetric.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){

    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            replaceFragment(BerandaFragment())
        }

        val openFragment = intent.getStringExtra("openFragment")
        if (openFragment == "ElektronikFragment") {
            replaceFragment(ElektronikFragment())
            binding.bottomNav.menu.findItem(R.id.elektronik).isChecked=true
        }

        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.beranda -> replaceFragment(BerandaFragment())
                R.id.elektronik -> replaceFragment(ElektronikFragment())
                R.id.catatan -> replaceFragment(CatatanFragment())
                R.id.profil -> replaceFragment(ProfilFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}