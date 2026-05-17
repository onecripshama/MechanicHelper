package com.example.mechanichelper.data.repository

import com.example.mechanichelper.data.api.CreateCarRequest
import com.example.mechanichelper.data.api.MechanicApi
import com.example.mechanichelper.data.api.UpdateCarRequest
import com.example.mechanichelper.domain.CarRepository
import com.example.mechanichelper.domain.UserPreferencesRepository
import com.example.mechanichelper.domain.model.Car
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CarRepositoryImpl @Inject constructor(
    private val mechanicApi: MechanicApi,
    private val userPreferences: UserPreferencesRepository
) : CarRepository {

    private val _cars = MutableStateFlow<List<Car>>(emptyList())

    override fun getCars(): Flow<List<Car>> = _cars.asStateFlow()

    override suspend fun refresh() {
        if (userPreferences.getCurrentLogin() == null) {
            _cars.value = emptyList()
            return
        }
        _cars.value = mechanicApi.getCars().map { dto ->
            Car(id = dto.id, name = dto.name, mileage = dto.mileage)
        }
    }

    override suspend fun getCarById(id: String): Car? =
        _cars.value.find { it.id == id } ?: run {
            refresh()
            _cars.value.find { it.id == id }
        }

    override suspend fun addCar(car: Car) {
        if (userPreferences.getCurrentLogin() == null) return
        val created = mechanicApi.createCar(
            CreateCarRequest(name = car.name, mileage = car.mileage)
        )
        _cars.value = _cars.value + Car(
            id = created.id,
            name = created.name,
            mileage = created.mileage
        )
    }

    override suspend fun updateCar(car: Car) {
        if (userPreferences.getCurrentLogin() == null) return
        val updated = mechanicApi.updateCar(
            car.id,
            UpdateCarRequest(name = car.name, mileage = car.mileage)
        )
        _cars.value = _cars.value.map {
            if (it.id == car.id) {
                Car(id = updated.id, name = updated.name, mileage = updated.mileage)
            } else {
                it
            }
        }
    }

    override suspend fun deleteCar(id: String) {
        if (userPreferences.getCurrentLogin() == null) return
        mechanicApi.deleteCar(id)
        _cars.value = _cars.value.filter { it.id != id }
    }
}
