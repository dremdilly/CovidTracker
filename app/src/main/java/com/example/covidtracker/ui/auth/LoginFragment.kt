package com.example.covidtracker.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.covidtracker.App
import com.example.covidtracker.MainActivity
import com.example.covidtracker.PreferenceManager
import com.example.covidtracker.R
import com.example.covidtracker.databinding.FragmentLoginBinding
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding

    private val logTag = javaClass.simpleName

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentLoginBinding.bind(view)

        binding.signInBtn.setOnClickListener {
            sendLoginData()
        }
    }

    private fun sendLoginData() {
        val emailData = binding.emailEdit.text.toString()
        val passwordData = binding.passwordEdit.text.toString()

        if(!isValidInputData(emailData, passwordData)) {
            return
        }

        App.mAuth.signInWithEmailAndPassword(emailData, passwordData)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(logTag, "Successful authentication")
                    GlobalScope.launch {
                        PreferenceManager.updateUserID(it.result.user?.uid!!)
                        PreferenceManager.updateLastNotificationsCount(0)
                        PreferenceManager.preferencesFlow.collect {
                            if(it[PreferenceManager.PreferencesKeys.PHOTO] == null) {
                                PreferenceManager.updatePhoto("")
                            }
                        }
                    }
                    val intent = Intent(context, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    if(it.exception is FirebaseAuthInvalidCredentialsException || it.exception is FirebaseAuthInvalidUserException) {
                        val toast = Toast.makeText(context, "You have entered an invalid email or password", Toast.LENGTH_SHORT)
//                        (toast.view?.findViewById(android.R.id.message) as TextView).gravity = Gravity.CENTER
                        toast.show()
                    } else if(it.exception is FirebaseTooManyRequestsException) {
                        Toast.makeText(context, "Too many attempts to the server. Try again later.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Something went wrong ${it.result}", Toast.LENGTH_SHORT).show()
                    }

                    Log.d(logTag, "Login error")
                }
            }

        Log.d(logTag, "$emailData $passwordData")
    }

    private fun isValidInputData(email: String?, password: String?): Boolean {
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

        return true
    }
}