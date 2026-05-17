package com.example.mechanichelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mechanichelper.domain.CarRepository
import com.example.mechanichelper.domain.PhotoRepository
import com.example.mechanichelper.domain.model.Car
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class CarListItem(
    val car: Car,
    val photoUri: String?
)

@HiltViewModel
class CarListViewModel @Inject constructor(
    private val carRepository: CarRepository,
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val refreshTrigger = MutableStateFlow(0)

    val cars: StateFlow<List<CarListItem>> = combine(
        carRepository.getCars(),
        refreshTrigger
    ) { cars, _ ->
        cars.map { car ->
            CarListItem(
                car = car,
                photoUri = photoRepository.getSavedPhotoUri(car.id)?.toString()
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun refresh() {
        refreshTrigger.value++
    }

    fun addCar(name: String, mileage: Int) {
        viewModelScope.launch {
            carRepository.addCar(
                Car(
                    id = UUID.randomUUID().toString(),
                    name = name.trim(),
                    mileage = mileage
                )
            )
        }
    }

    fun updateCar(id: String, name: String, mileage: Int) {
        viewModelScope.launch {
            val car = carRepository.getCarById(id) ?: return@launch
            carRepository.updateCar(car.copy(name = name.trim(), mileage = mileage))
        }
    }

    fun deleteCar(id: String) {
        viewModelScope.launch {
            photoRepository.deletePhoto(id)
            carRepository.deleteCar(id)
        }
    }
}
