package com.example.covidtracker.ui.dashboard

import android.text.style.BackgroundColorSpan
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.covidtracker.App
import com.example.covidtracker.PreferenceManager
import com.example.covidtracker.R
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val _btBtnText = getBluetoothMode()

    private val _btBtnColor = MutableLiveData<Int>().apply {
        value = if(getBluetoothMode().value == "Enable Bluetooth") {
            0xFFD53939.toInt()
        } else {
            0xFF1D671D.toInt()
        }
    }

    private fun getBluetoothMode() = MutableLiveData<String>().apply{
        viewModelScope.launch {
            value = if(PreferenceManager.getBluetoothMode()) {
                "Disable Bluetooth"
            } else {
                "Enable Bluetooth"
            }
        }
    }

    fun updateBtBtnText(text: String) {
        _btBtnText.value = text
    }

    fun updateBtBtnColor(color: Int) {
        _btBtnColor.value = color
    }

    val btBtnText: LiveData<String> = _btBtnText
    val btBtnColor: LiveData<Int> = _btBtnColor
}