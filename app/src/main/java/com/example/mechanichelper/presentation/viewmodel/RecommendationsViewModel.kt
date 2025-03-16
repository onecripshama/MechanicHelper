package com.example.mechanichelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mechanichelper.domain.RecommendationsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendationsViewModel @Inject constructor(
    private val recommendationsRepository: RecommendationsRepository
) : ViewModel() {

    val recommendations: StateFlow<List<String>> = recommendationsRepository
        .getRecommendations()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun addRecommendation(recommendation: String) {
        viewModelScope.launch {
            recommendationsRepository.addRecommendation(recommendation)
        }
    }

    fun deleteRecommendations(selectedIndices: List<Int>) {
        viewModelScope.launch {
            recommendationsRepository.deleteRecommendations(selectedIndices)
        }
    }
}
