package com.example.mechanichelper.domain

import kotlinx.coroutines.flow.Flow

interface RecommendationsRepository {
    fun getRecommendations(): Flow<List<String>>
    suspend fun addRecommendation(recommendation: String)
    suspend fun deleteRecommendations(selectedIndices: List<Int>)
}
