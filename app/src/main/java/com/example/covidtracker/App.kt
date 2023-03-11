package com.example.covidtracker

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class App : Application() {
    companion object {
        lateinit var database: FirebaseDatabase
        lateinit var mAuth: FirebaseAuth
    }

    override fun onCreate() {
        super.onCreate()
        database = FirebaseDatabase.getInstance("https://soy-braid-314517-default-rtdb.europe-west1.firebasedatabase.app")
        mAuth = FirebaseAuth.getInstance()
        PreferenceManager.initPreferences(applicationContext)
    }

    suspend fun getUser(): DatabaseReference {
        return App.database.getReference("users")
            .child(PreferenceManager.getUserId())
    }
}