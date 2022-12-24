package com.kodego.app.istak_inventory

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.kodego.app.istak_inventory.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBarWithNavController(findNavController(R.id.fragment))
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.white)))
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

//Source:
// KodeGo : https://youtu.be/ke6rCVy5XHQ
//          https://youtu.be/bG01Kryhi5o
//          https://youtu.be/E6AAkUumXNk
//External: https://www.youtube.com/playlist?list=PLSrm9z4zp4mEPOfZNV9O-crOhoMa0G2-o
//          Add Menu Provider: https://developer.android.com/jetpack/androidx/releases/activity#1.4.0-alpha01