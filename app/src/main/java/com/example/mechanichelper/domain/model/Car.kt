package com.example.mechanichelper.domain.model

data class Car(
    val id: String,
    val name: String,
    val mileage: Int,
    val photoPath: String? = null
)
