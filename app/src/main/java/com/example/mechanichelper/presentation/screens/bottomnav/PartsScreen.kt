package com.example.mechanichelper.presentation.screens.bottomnav

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mechanichelper.data.api.Part
import com.example.mechanichelper.presentation.viewmodel.PartsViewModel

@Composable
fun PartsScreen(
    viewModel: PartsViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchQuery by viewModel.searchQuery
    val parts by viewModel.parts
    val hasError by viewModel.hasError
    val hasSearched by viewModel.hasSearched
    val showNoResults by viewModel.showNoResults

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text(
            text = "Запчасти",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        SearchField(
            searchQuery = searchQuery,
            onValueChange = viewModel::updateSearchQuery,
            onSearch = {
                viewModel.search()
                keyboardController?.hide()
            },
            onClear = {
                viewModel.clear()
                keyboardController?.hide()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ContentState(
            parts = parts,
            hasError = hasError,
            hasSearched = hasSearched,
            showNoResults = showNoResults,
            onRetry = viewModel::search
        )
    }
}

@Composable
private fun SearchField(
    searchQuery: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onValueChange,
            label = { Text("Поиск запчастей") },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = onSearch) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Поиск"
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (searchQuery.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onClear,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Очистить")
            }
        }
    }
}

@Composable
private fun ContentState(
    parts: List<Part>,
    hasError: Boolean,
    hasSearched: Boolean,
    showNoResults: Boolean,
    onRetry: () -> Unit
) {
    when {
        hasError -> ErrorRetrySection(onRetry = onRetry)
        showNoResults -> NoResultsSection()
        parts.isNotEmpty() || !hasSearched -> PartsList(parts = parts)
    }
}

@Composable
private fun ErrorRetrySection(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ошибка поиска. Попробуйте обновить.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = onRetry) {
            Text("Обновить")
        }
    }
}

@Composable
private fun NoResultsSection() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Ничего не найдено",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun PartsList(parts: List<Part>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(parts) { part ->
            PartItem(part = part)
        }
    }
}

@Composable
private fun PartItem(part: Part) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = part.title,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = part.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

