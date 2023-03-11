package com.example.covidtracker.models

import android.text.format.DateFormat.format

data class StatusModel(
    val color: String? = "blue",
    val date: String? = "__.__.____",
    val info: String? = "You don't have PCR result",
    val updated: String? = "Updated: Today, ${format("HH:mm", System.currentTimeMillis())}",
)