package com.example.mechanichelper.domain

import com.example.mechanichelper.domain.model.TextListItem
import kotlinx.coroutines.flow.Flow

interface RemindersRepository {
    fun getReminders(): Flow<List<TextListItem>>
    suspend fun refresh()
    suspend fun addReminder(reminder: String)
    suspend fun deleteReminders(ids: List<String>)
}
