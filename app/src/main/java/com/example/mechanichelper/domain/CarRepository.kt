package com.example.mechanichelper.domain

import com.example.mechanichelper.domain.model.Car
import kotlinx.coroutines.flow.Flow

interface CarRepository {
    fun getCars(): Flow<List<Car>>
    suspend fun getCarById(id: String): Car?
    suspend fun addCar(car: Car)
    suspend fun updateCar(car: Car)
    suspend fun deleteCar(id: String)
}
