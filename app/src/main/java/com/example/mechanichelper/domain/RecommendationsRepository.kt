package com.example.mechanichelper.domain

import kotlinx.coroutines.flow.Flow

interface RecommendationsRepository {
    fun getRecommendations(carId: String): Flow<List<String>>
    suspend fun addRecommendation(carId: String, recommendation: String)
    suspend fun deleteRecommendations(carId: String, selectedIndices: List<Int>)
}
