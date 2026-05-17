package com.example.mechanichelper.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mechanichelper.domain.RemindersRepository
import com.example.mechanichelper.domain.model.TextListItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val remindersRepository: RemindersRepository
) : ViewModel() {

    val reminders: StateFlow<List<TextListItem>> = remindersRepository
        .getReminders()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch { remindersRepository.refresh() }
    }

    fun addReminder(reminder: String) {
        viewModelScope.launch {
            remindersRepository.addReminder(reminder)
        }
    }

    fun deleteReminders(ids: List<String>) {
        viewModelScope.launch {
            remindersRepository.deleteReminders(ids)
        }
    }
}
