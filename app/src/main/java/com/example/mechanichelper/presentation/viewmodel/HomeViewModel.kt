package com.example.mechanichelper.presentation.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mechanichelper.domain.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _carMileage = MutableStateFlow("0")
    val carMileage: StateFlow<String> = _carMileage.asStateFlow()

    private val _recommendation = MutableStateFlow("")
    val recommendation: StateFlow<String> = _recommendation.asStateFlow()

    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri: StateFlow<Uri?> = _photoUri.asStateFlow()

    init {
        loadLastPhoto()
        updateRecommendation()
    }

    fun updateMileage(newMileage: String) {
        _carMileage.value = newMileage
        updateRecommendation()
    }

    private fun updateRecommendation() {
        val mileage = _carMileage.value.toIntOrNull() ?: 0
        _recommendation.value = when {
            mileage < 5000 -> "Проверяйте уровень жидкостей регулярно"
            mileage in 5000..15000 -> "Замена масла и фильтров"
            mileage in 15000..30000 -> "Диагностика тормозной системы"
            mileage in 30000..50000 -> "Комплексное ТО"
            else -> "Полное техническое обслуживание"
        }
    }

    fun takePhoto() {
        viewModelScope.launch {
            try {
                // Сброс значения перед созданием нового файла
                _photoUri.value = null

                val file = photoRepository.createImageFile()
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )

                // Устанавливаем новый URI с временной меткой
                _photoUri.value = Uri.parse("$uri?t=${System.currentTimeMillis()}")
            } catch (e: Exception) {
                Log.e("Camera", "Error: ${e.message}")
            }
        }
    }

    fun loadLastPhoto() {
        viewModelScope.launch {
            // Добавляем задержку для обхода кэша файловой системы
            delay(150)
            _photoUri.value = photoRepository.getLastSavedPhotoUri()?.let {
                Uri.parse("$it?t=${System.currentTimeMillis()}")
            }
        }
    }
}