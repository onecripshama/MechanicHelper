package com.example.mechanichelper.presentation.screens.bottomnav

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mechanichelper.presentation.components.BottomNavScreenLayout
import com.example.mechanichelper.presentation.components.EditableItemsList
import com.example.mechanichelper.presentation.viewmodel.RemindersViewModel

@Composable
fun RemindersScreen(
    viewModel: RemindersViewModel = hiltViewModel()
) {
    val reminders by viewModel.reminders.collectAsState()

    BottomNavScreenLayout(title = "Напоминания") {
        EditableItemsList(
            items = reminders,
            addButtonText = "Добавить напоминание",
            dialogTitle = "Новое напоминание",
            dialogFieldLabel = "Введите напоминание",
            onAdd = viewModel::addReminder,
            onDeleteSelected = viewModel::deleteReminders,
            modifier = Modifier.weight(1f)
        )
    }
}
