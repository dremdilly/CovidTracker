package com.example.covidtracker.ui.auth

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.covidtracker.App
import com.example.covidtracker.R
import com.example.covidtracker.databinding.FragmentSignupBinding
import com.example.covidtracker.models.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import java.util.regex.Pattern

class SignupFragment : Fragment(R.layout.fragment_signup) {
    private lateinit var binding: FragmentSignupBinding

    private val logTag = javaClass.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSignupBinding.bind(view)

        binding.signUpBtn.setOnClickListener {
            sendRegisterData()
        }
    }

    // Send registration user data to the firebase auth and database
    private fun sendRegisterData() {
        val nameData = binding.nameEdit.text.toString()
        val emailData = binding.emailEdit.text.toString()
        val passwordData = binding.passwordEdit.text.toString()
        val repasswordData = binding.rePasswordEdit.text.toString()

        if(!isValidInputData(nameData, emailData, passwordData, repasswordData)) {
            return
        }

        App.mAuth.createUserWithEmailAndPassword(emailData, passwordData)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(logTag, "Successful registration")
                    writeNewUser(it.result.user?.uid!!, nameData, emailData)

                    val action = SignupFragmentDirections.actionSignupFragmentToLoginFragment()
                    if (findNavController().currentDestination?.id == R.id.signupFragment) {
                        findNavController().navigate(action)
                    }
                } else {
                    if(it.exception is FirebaseAuthUserCollisionException) {
                        Toast.makeText(context, "This user already exists", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                    Log.d(logTag, "Registration error")
                }
            }

        Log.d(logTag, "$emailData $passwordData")
    }

    // Validate if input user data is correct
    private fun isValidInputData(name: String?, email: String?, password: String?, repassword: String?): Boolean {
        if(name.isNullOrEmpty()) {
            Toast.makeText(context, "Please input your name", Toast.LENGTH_SHORT).show()
            return false
        }

        if(email.isNullOrEmpty()) {
            Toast.makeText(context, "Please input email address", Toast.LENGTH_SHORT).show()
            return false
        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(context, "Please input valid email address", Toast.LENGTH_SHORT).show()
            return false
        }

        if(password.isNullOrEmpty()) {
            Toast.makeText(context, "Please input password", Toast.LENGTH_SHORT).show()
            return false
        }

        val passwordREGEX = Pattern.compile("^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                ".{8,}" +               //at least 8 characters
                "$");
        if(!passwordREGEX.matcher(password).matches()) {
            Toast.makeText(context, "Password should have at least 1 digit, 1 lower case and upper case letters and should be at least 8 characters", Toast.LENGTH_LONG).show()
            return false
        }

        if(repassword.isNullOrEmpty() || repassword != password) {
            Toast.makeText(context, "Entered passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    //Write new user into the database
    private fun writeNewUser(userId: String, name: String, email: String) {
        val user = User(name, email)

        App.database.getReference("users").child(userId).setValue(user)
    }
}