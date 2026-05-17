package com.example.mechanichelper.domain

import kotlinx.coroutines.flow.Flow

interface RemindersRepository {
    fun getReminders(): Flow<List<String>>
    suspend fun refresh()
    suspend fun addReminder(reminder: String)
    suspend fun deleteReminders(selectedIndices: List<Int>)
}
