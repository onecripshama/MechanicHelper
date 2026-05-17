package com.example.mechanichelper.data.repository

import android.content.Context
import androidx.core.content.edit
import com.example.mechanichelper.domain.CarRepository
import com.example.mechanichelper.domain.model.Car
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CarRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CarRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val _cars = MutableStateFlow(loadCars())

    override fun getCars(): Flow<List<Car>> = _cars

    override suspend fun getCarById(id: String): Car? =
        _cars.value.find { it.id == id }

    override suspend fun addCar(car: Car) {
        val updated = _cars.value + car
        _cars.value = updated
        saveCars(updated)
    }

    override suspend fun updateCar(car: Car) {
        val updated = _cars.value.map { if (it.id == car.id) car else it }
        _cars.value = updated
        saveCars(updated)
    }

    override suspend fun deleteCar(id: String) {
        val updated = _cars.value.filter { it.id != id }
        _cars.value = updated
        saveCars(updated)
    }

    private fun loadCars(): List<Car> {
        val json = prefs.getString(KEY_CARS, null)
        return if (json != null) {
            val type = object : TypeToken<List<Car>>() {}.type
            gson.fromJson(json, type)
        } else {
            defaultCars().also { saveCars(it) }
        }
    }

    private fun saveCars(cars: List<Car>) {
        prefs.edit { putString(KEY_CARS, gson.toJson(cars)) }
    }

    private fun defaultCars(): List<Car> = listOf(
        Car(id = UUID.randomUUID().toString(), name = "Toyota Camry", mileage = 45_000),
        Car(id = UUID.randomUUID().toString(), name = "BMW X5", mileage = 120_000),
        Car(id = UUID.randomUUID().toString(), name = "Lada Vesta", mileage = 85_000)
    )

    companion object {
        private const val PREFS_NAME = "cars_prefs"
        private const val KEY_CARS = "cars_key"
    }
}
