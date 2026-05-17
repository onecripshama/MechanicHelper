package com.example.mechanichelper.domain

import com.example.mechanichelper.domain.model.TextListItem
import kotlinx.coroutines.flow.Flow

interface RecommendationsRepository {
    fun getRecommendations(carId: String): Flow<List<TextListItem>>
    suspend fun refresh(carId: String)
    suspend fun addRecommendation(carId: String, recommendation: String)
    suspend fun deleteRecommendations(carId: String, ids: List<String>)
    fun clearForCar(carId: String)
}
