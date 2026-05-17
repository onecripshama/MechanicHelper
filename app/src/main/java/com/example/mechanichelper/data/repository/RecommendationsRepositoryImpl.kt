package com.example.mechanichelper.data.repository

import com.example.mechanichelper.data.api.CreateRecommendationRequest
import com.example.mechanichelper.data.api.DeleteRecommendationsRequest
import com.example.mechanichelper.data.api.MechanicApi
import com.example.mechanichelper.domain.RecommendationsRepository
import com.example.mechanichelper.domain.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationsRepositoryImpl @Inject constructor(
    private val mechanicApi: MechanicApi,
    private val userPreferences: UserPreferencesRepository
) : RecommendationsRepository {

    private val cache = MutableStateFlow<Map<String, List<RecommendationEntry>>>(emptyMap())

    override fun getRecommendations(carId: String): Flow<List<String>> =
        cache.map { map -> map[carId].orEmpty().map { it.text } }

    override suspend fun refresh(carId: String) {
        if (userPreferences.getCurrentLogin() == null) {
            cache.value = cache.value - carId
            return
        }
        val items = mechanicApi.getRecommendations(carId).map { RecommendationEntry(it.id, it.text) }
        cache.value = cache.value + (carId to items)
    }

    override suspend fun addRecommendation(carId: String, recommendation: String) {
        if (userPreferences.getCurrentLogin() == null) return
        val created = mechanicApi.createRecommendation(
            carId,
            CreateRecommendationRequest(text = recommendation)
        )
        val current = cache.value[carId].orEmpty()
        cache.value = cache.value + (carId to (current + RecommendationEntry(created.id, created.text)))
    }

    override suspend fun deleteRecommendations(carId: String, selectedIndices: List<Int>) {
        if (userPreferences.getCurrentLogin() == null) return
        val current = cache.value[carId].orEmpty()
        val ids = selectedIndices.mapNotNull { index -> current.getOrNull(index)?.id }
        if (ids.isEmpty()) return
        mechanicApi.deleteRecommendations(carId, DeleteRecommendationsRequest(ids = ids))
        cache.value = cache.value + (carId to current.filterIndexed { index, _ ->
            !selectedIndices.contains(index)
        })
    }

    private data class RecommendationEntry(val id: String, val text: String)
}
