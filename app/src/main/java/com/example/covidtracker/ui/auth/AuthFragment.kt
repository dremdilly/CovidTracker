package com.example.covidtracker.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.covidtracker.R
import com.example.covidtracker.databinding.ActivityMainBinding
import com.example.covidtracker.databinding.FragmentAuthBinding
import com.example.covidtracker.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AuthFragment : Fragment(R.layout.fragment_auth) {
    private lateinit var binding: FragmentAuthBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAuthBinding.bind(view)

        binding.signUpBtn.setOnClickListener {
            val action = AuthFragmentDirections.actionAuthFragmentToSignupFragment()
            if(findNavController().currentDestination?.id == R.id.authFragment) {
                findNavController().navigate(action)
            }
        }

        binding.signInBtn.setOnClickListener {
            val action = AuthFragmentDirections.actionAuthFragmentToLoginFragment()
            if(findNavController().currentDestination?.id == R.id.authFragment) {
                findNavController().navigate(action)
            }
        }
    }
}