package com.example.covidtracker.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.covidtracker.App
import com.example.covidtracker.MainActivity
import com.example.covidtracker.PreferenceManager
import com.example.covidtracker.models.StatusModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class HomeViewModel() : ViewModel() {
    private val _userNameText = getValue("username")

    private val _emailText = getValue("email")

    private val _locationText = getValue("location")

    private val _statusText = getStatusValue("color")
    private val _statusInfoText =getStatusValue("info")
    private val _statusDateText = getStatusValue("date")
    private val _statusUpdatedText =getStatusValue("updated")

    private fun getValue(path: String) = MutableLiveData<String>().apply{
        viewModelScope.launch {
            App.database.getReference("users")
                .child(PreferenceManager.getUserId())
                .child(path).get().addOnSuccessListener {
                    value = it.value.toString()
                }
        }
    }

    private fun getStatusValue(path: String) = MutableLiveData<String>().apply{
        viewModelScope.launch {
            App.database.getReference("users")
                .child(PreferenceManager.getUserId())
                .child("status").child(path).get().addOnSuccessListener {
                    value = it.value.toString()
                }
        }
    }

    fun setStatus(status: StatusModel) {
        viewModelScope.launch {
            App.database.getReference("users")
                .child(PreferenceManager.getUserId())
                .child("status").setValue(status)
        }

        _statusText.value = status.color
        _statusInfoText.value = status.info
        _statusDateText.value = status.date
        _statusUpdatedText.value = status.updated

    }

    fun updateStatus(text: String) {
        _statusText.value = text
    }

    fun updateUsername(text: String) {
        _userNameText.value = text
    }

    fun updateLocation(text: String) {
        _locationText.value = text
    }



    val userNameText: LiveData<String> = _userNameText
    val emailText: LiveData<String> = _emailText
    val locationText: LiveData<String> = _locationText
    val statusText: LiveData<String> = _statusText
    val statusInfoText: LiveData<String> = _statusInfoText
    val statusDateText: LiveData<String> = _statusDateText
    val statusUpdatedText: LiveData<String> = _statusUpdatedText
}