package com.example.mechanichelper.data.repository

import android.content.Context
import androidx.core.content.edit
import com.example.mechanichelper.domain.RecommendationsRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : RecommendationsRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val allRecommendations = MutableStateFlow(loadAll())

    override fun getRecommendations(carId: String): Flow<List<String>> =
        allRecommendations.map { it[carId].orEmpty() }

    override suspend fun addRecommendation(carId: String, recommendation: String) {
        val updated = allRecommendations.value.toMutableMap()
        updated[carId] = updated[carId].orEmpty() + recommendation
        allRecommendations.value = updated
        saveAll(updated)
    }

    override suspend fun deleteRecommendations(carId: String, selectedIndices: List<Int>) {
        val updated = allRecommendations.value.toMutableMap()
        updated[carId] = updated[carId].orEmpty().filterIndexed { index, _ ->
            !selectedIndices.contains(index)
        }
        allRecommendations.value = updated
        saveAll(updated)
    }

    private fun loadAll(): Map<String, List<String>> {
        val json = prefs.getString(KEY_RECOMMENDATIONS, null) ?: return emptyMap()
        val type = object : TypeToken<Map<String, List<String>>>() {}.type
        return gson.fromJson(json, type) ?: emptyMap()
    }

    private fun saveAll(data: Map<String, List<String>>) {
        prefs.edit { putString(KEY_RECOMMENDATIONS, gson.toJson(data)) }
    }

    companion object {
        private const val PREFS_NAME = "car_recommendations"
        private const val KEY_RECOMMENDATIONS = "car_recommendations_key"
    }
}
