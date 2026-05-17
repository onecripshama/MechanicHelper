package com.example.mechanichelper.data.repository

import com.example.mechanichelper.data.api.CreateRecommendationRequest
import com.example.mechanichelper.data.api.DeleteByIdRequest
import com.example.mechanichelper.data.api.MechanicApi
import com.example.mechanichelper.data.network.requireSuccess
import com.example.mechanichelper.domain.RecommendationsRepository
import com.example.mechanichelper.domain.UserPreferencesRepository
import com.example.mechanichelper.domain.model.TextListItem
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

    private val cache = MutableStateFlow<Map<String, List<TextListItem>>>(emptyMap())

    override fun getRecommendations(carId: String): Flow<List<TextListItem>> =
        cache.map { map -> map[carId].orEmpty() }

    override suspend fun refresh(carId: String) {
        if (userPreferences.getCurrentLogin() == null) {
            cache.value = cache.value - carId
            return
        }
        val items = mechanicApi.getRecommendations(carId).map { dto ->
            TextListItem(id = dto.id, text = dto.text)
        }
        cache.value = cache.value + (carId to items)
    }

    override suspend fun addRecommendation(carId: String, recommendation: String) {
        if (userPreferences.getCurrentLogin() == null) return
        val created = mechanicApi.createRecommendation(
            carId,
            CreateRecommendationRequest(text = recommendation)
        )
        val current = cache.value[carId].orEmpty()
        cache.value = cache.value + (carId to (current + TextListItem(created.id, created.text)))
    }

    override suspend fun deleteRecommendations(carId: String, ids: List<String>) {
        if (userPreferences.getCurrentLogin() == null || ids.isEmpty()) return

        val idsToDelete = ids.toSet()
        val current = cache.value[carId].orEmpty()

        for (id in idsToDelete) {
            mechanicApi.deleteRecommendation(carId, DeleteByIdRequest(id = id)).requireSuccess()
        }
        cache.value = cache.value + (carId to current.filter { it.id !in idsToDelete })
    }

    override fun clearForCar(carId: String) {
        cache.value = cache.value - carId
    }
}
