package com.example.app


import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity

import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.ui.setupWithNavController


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
//        Search news
//        ------------------------------------------------

        val navController = findNavController(R.id.fragmentContainerView3)
        val buttonNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        buttonNavigationView.setupWithNavController(navController)
        
    }
}
