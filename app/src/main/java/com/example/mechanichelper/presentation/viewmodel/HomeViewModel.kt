package com.example.mechanichelper.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mechanichelper.domain.CarRepository
import com.example.mechanichelper.domain.PhotoRepository
import com.example.mechanichelper.domain.RecommendationsRepository
import com.example.mechanichelper.domain.model.TextListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val photoRepository: PhotoRepository,
    private val carRepository: CarRepository,
    private val recommendationsRepository: RecommendationsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val carId: String = savedStateHandle.get<String>("carId") ?: ""

    private val _carName = MutableStateFlow("")
    val carName: StateFlow<String> = _carName.asStateFlow()

    private val _carMileage = MutableStateFlow("0")
    val carMileage: StateFlow<String> = _carMileage.asStateFlow()

    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri: StateFlow<Uri?> = _photoUri.asStateFlow()

    val recommendations: StateFlow<List<TextListItem>> = recommendationsRepository
        .getRecommendations(carId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadCar()
        loadLastPhoto()
        viewModelScope.launch {
            recommendationsRepository.refresh(carId)
        }
    }

    private fun loadCar() {
        viewModelScope.launch {
            val car = carRepository.getCarById(carId) ?: return@launch
            _carName.value = car.name
            _carMileage.value = car.mileage.toString()
        }
    }

    fun addRecommendation(text: String) {
        viewModelScope.launch {
            recommendationsRepository.addRecommendation(carId, text)
        }
    }

    fun deleteRecommendations(ids: List<String>) {
        viewModelScope.launch {
            recommendationsRepository.deleteRecommendations(carId, ids)
        }
    }

    fun takePhoto() {
        viewModelScope.launch {
            try {
                photoRepository.deletePhoto(carId)
                _photoUri.value = null

                val file = photoRepository.createImageFile(carId)
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )

                _photoUri.value = Uri.parse("$uri?t=${System.currentTimeMillis()}")
            } catch (e: Exception) {
                Log.e("Camera", "Error: ${e.message}")
            }
        }
    }

    fun loadLastPhoto() {
        viewModelScope.launch {
            delay(150)
            _photoUri.value = photoRepository.getSavedPhotoUri(carId)?.let {
                Uri.parse("$it?t=${System.currentTimeMillis()}")
            }
        }
    }
}
