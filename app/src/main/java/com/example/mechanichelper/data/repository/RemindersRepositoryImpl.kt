package com.example.mechanichelper.data.repository

import com.example.mechanichelper.data.api.CreateReminderRequest
import com.example.mechanichelper.data.api.DeleteByIdRequest
import com.example.mechanichelper.data.api.MechanicApi
import com.example.mechanichelper.data.network.requireSuccess
import com.example.mechanichelper.domain.RemindersRepository
import com.example.mechanichelper.domain.UserPreferencesRepository
import com.example.mechanichelper.domain.model.TextListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemindersRepositoryImpl @Inject constructor(
    private val mechanicApi: MechanicApi,
    private val userPreferences: UserPreferencesRepository
) : RemindersRepository {

    private val _reminders = MutableStateFlow<List<TextListItem>>(emptyList())

    override fun getReminders(): Flow<List<TextListItem>> = _reminders.asStateFlow()

    override suspend fun refresh() {
        if (userPreferences.getCurrentLogin() == null) {
            _reminders.value = emptyList()
            return
        }
        _reminders.value = mechanicApi.getReminders().map { dto ->
            TextListItem(id = dto.id, text = dto.text)
        }
    }

    override suspend fun addReminder(reminder: String) {
        if (userPreferences.getCurrentLogin() == null) return
        val created = mechanicApi.createReminder(CreateReminderRequest(text = reminder))
        _reminders.value = _reminders.value + TextListItem(id = created.id, text = created.text)
    }

    override suspend fun deleteReminders(ids: List<String>) {
        if (userPreferences.getCurrentLogin() == null || ids.isEmpty()) return

        val idsToDelete = ids.toSet()
        for (id in idsToDelete) {
            mechanicApi.deleteReminder(DeleteByIdRequest(id = id)).requireSuccess()
        }
        _reminders.value = _reminders.value.filter { it.id !in idsToDelete }
    }
}
