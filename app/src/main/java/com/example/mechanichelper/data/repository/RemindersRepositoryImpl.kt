package com.example.mechanichelper.data.repository

import android.content.Context
import androidx.core.content.edit
import com.example.mechanichelper.domain.RemindersRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemindersRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : RemindersRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()
    private val remindersFlow = MutableStateFlow(loadReminders())

    private fun loadReminders(): List<String> {
        val json = prefs.getString(KEY_REMINDERS, null)
        if (json != null) {
            val type = object : TypeToken<List<String>>() {}.type
            return gson.fromJson(json, type) ?: emptyList()
        }
        return migrateFromLegacyRecommendations()
    }

    private fun migrateFromLegacyRecommendations(): List<String> {
        val legacyPrefs = context.getSharedPreferences("recommendations", Context.MODE_PRIVATE)
        val legacyJson = legacyPrefs.getString("recommendations_key", null) ?: return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        val legacyList = gson.fromJson<List<String>>(legacyJson, type) ?: return emptyList()
        if (legacyList.isNotEmpty()) {
            saveReminders(legacyList)
            legacyPrefs.edit { remove("recommendations_key") }
        }
        return legacyList
    }

    private fun saveReminders(list: List<String>) {
        prefs.edit { putString(KEY_REMINDERS, gson.toJson(list)) }
    }

    override fun getReminders(): Flow<List<String>> = remindersFlow

    override suspend fun addReminder(reminder: String) {
        val updated = remindersFlow.value + reminder
        remindersFlow.value = updated
        saveReminders(updated)
    }

    override suspend fun deleteReminders(selectedIndices: List<Int>) {
        val updated = remindersFlow.value.filterIndexed { index, _ ->
            !selectedIndices.contains(index)
        }
        remindersFlow.value = updated
        saveReminders(updated)
    }

    companion object {
        private const val PREFS_NAME = "reminders"
        private const val KEY_REMINDERS = "reminders_key"
    }
}
