package com.example.mechanichelper.data.repository

import com.example.mechanichelper.data.api.CreateReminderRequest
import com.example.mechanichelper.data.api.DeleteRemindersRequest
import com.example.mechanichelper.data.api.MechanicApi
import com.example.mechanichelper.domain.RemindersRepository
import com.example.mechanichelper.domain.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemindersRepositoryImpl @Inject constructor(
    private val mechanicApi: MechanicApi,
    private val userPreferences: UserPreferencesRepository
) : RemindersRepository {

    private val _reminders = MutableStateFlow<List<ReminderEntry>>(emptyList())

    override fun getReminders(): Flow<List<String>> =
        _reminders.map { entries -> entries.map { it.text } }

    override suspend fun refresh() {
        if (userPreferences.getCurrentLogin() == null) {
            _reminders.value = emptyList()
            return
        }
        _reminders.value = mechanicApi.getReminders().map { ReminderEntry(it.id, it.text) }
    }

    override suspend fun addReminder(reminder: String) {
        if (userPreferences.getCurrentLogin() == null) return
        val created = mechanicApi.createReminder(CreateReminderRequest(text = reminder))
        _reminders.value = _reminders.value + ReminderEntry(created.id, created.text)
    }

    override suspend fun deleteReminders(selectedIndices: List<Int>) {
        if (userPreferences.getCurrentLogin() == null) return
        val ids = selectedIndices.mapNotNull { index -> _reminders.value.getOrNull(index)?.id }
        if (ids.isEmpty()) return
        mechanicApi.deleteReminders(DeleteRemindersRequest(ids = ids))
        _reminders.value = _reminders.value.filterIndexed { index, _ ->
            !selectedIndices.contains(index)
        }
    }

    private data class ReminderEntry(val id: String, val text: String)
}
