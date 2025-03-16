package com.example.mechanichelper.data.repository

import android.content.Context
import androidx.core.content.edit
import com.example.mechanichelper.domain.RecommendationsRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : RecommendationsRepository {

    private val sharedPreferences = context.getSharedPreferences("recommendations", Context.MODE_PRIVATE)
    private val recommendationsKey = "recommendations_key"
    private val gson = Gson()

    private val _recommendationsFlow = MutableStateFlow<List<String>>(loadRecommendations())

    private fun loadRecommendations(): List<String> {
        val json = sharedPreferences.getString(recommendationsKey, null)
        return if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    private fun saveRecommendations(list: List<String>) {
        val json = gson.toJson(list)
        sharedPreferences.edit {
            putString(recommendationsKey, json)
        }
    }

    override fun getRecommendations(): Flow<List<String>> = _recommendationsFlow

    override suspend fun addRecommendation(recommendation: String) {
        val newList = _recommendationsFlow.value + recommendation
        _recommendationsFlow.value = newList
        saveRecommendations(newList)
    }

    override suspend fun deleteRecommendations(selectedIndices: List<Int>) {
        val newList = _recommendationsFlow.value.filterIndexed { index, _ ->
            !selectedIndices.contains(index)
        }
        _recommendationsFlow.value = newList
        saveRecommendations(newList)
    }
}
