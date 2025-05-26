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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mechanichelper.data.api.User
import com.example.mechanichelper.presentation.viewmodel.UsersViewModel

@Composable
fun UsersScreen(
    viewModel: UsersViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchQuery by viewModel.searchQuery
    val users by viewModel.users
    val hasError by viewModel.hasError
    val hasSearched by viewModel.hasSearched
    val showNoResults by viewModel.showNoResults
    val isSearching by viewModel.isSearching.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Text(
            text = "Клиенты",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        SearchField(
            searchQuery = searchQuery,
            searchHistory = viewModel.searchHistory,
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
            users = users,
            hasError = hasError,
            hasSearched = hasSearched,
            showNoResults = showNoResults,
            isSearching = isSearching,
            onRetry = viewModel::search
        )
    }
}



@Composable
private fun SearchField(
    searchQuery: String,
    searchHistory: List<String>,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newText ->
                onValueChange(newText)
                expanded = newText.isEmpty()
            },
            label = { Text("Поиск клиентов") },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    expanded = focusState.isFocused
                            && searchQuery.isEmpty()
                            && searchHistory.isNotEmpty()
                },
            trailingIcon = {
                IconButton(onClick = {
                    onSearch()
                    focusManager.clearFocus()
                    expanded = false
                }) {
                    Icon(Icons.Filled.Search, "Поиск")
                }
            }
        )

        DropdownMenu(
            expanded = expanded && searchHistory.isNotEmpty(),
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            searchHistory.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onValueChange(item)
                        onSearch()
                        expanded = false
                    }
                )
            }
        }

        if (searchQuery.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    onClear()
                    expanded = false
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Очистить")
            }
        }
    }
}

@Composable
private fun ContentState(
    users: List<User>,
    hasError: Boolean,
    hasSearched: Boolean,
    showNoResults: Boolean,
    isSearching: Boolean,
    onRetry: () -> Unit
) {
    when {
        isSearching -> LoadingSection()
        hasError -> ErrorRetrySection(onRetry = onRetry)
        showNoResults -> NoResultsSection()
        users.isNotEmpty() || !hasSearched -> UsersList(users = users)
    }
}

@Composable
private fun LoadingSection() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
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
private fun UsersList(users: List<User>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(users) { user ->
            UserItem(user = user)
        }
    }
}


@Composable
private fun UserItem(user: User) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${user.firstName} ${user.lastName}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = user.phone,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
