package com.example.covidtracker.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.covidtracker.R
import com.example.covidtracker.databinding.ActivityAuthBinding
import com.example.covidtracker.databinding.ActivityMainBinding
import com.example.covidtracker.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance("https://soy-braid-314517-default-rtdb.europe-west1.firebasedatabase.app")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navAuthFragment = supportFragmentManager
            .findFragmentById(R.id.nav_auth_fragment) as NavHostFragment

        navController = navAuthFragment.navController
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}