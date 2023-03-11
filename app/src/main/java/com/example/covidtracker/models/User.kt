package com.example.covidtracker.models

data class User(
    val username: String? = null,
    val email: String? = null,
    val status: StatusModel? = StatusModel(),
)