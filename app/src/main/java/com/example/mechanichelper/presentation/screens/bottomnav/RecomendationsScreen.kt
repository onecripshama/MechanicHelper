package com.example.mechanichelper.presentation.screens.bottomnav

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mechanichelper.presentation.viewmodel.RecommendationsViewModel

@Composable
fun RecommendationsScreen(
    recommendationsViewModel: RecommendationsViewModel = hiltViewModel()
) {
    val recommendations by recommendationsViewModel.recommendations.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var recommendationText by remember { mutableStateOf("") }
    var selectedRecommendations by remember { mutableStateOf(setOf<Int>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Рекомендации",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            itemsIndexed(recommendations) { index, recommendation ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            colors = CheckboxDefaults.colors(
                                MaterialTheme.colorScheme.onTertiaryContainer
                            ),
                            checked = selectedRecommendations.contains(index),
                            onCheckedChange = { isChecked ->
                                selectedRecommendations = if (isChecked) {
                                    selectedRecommendations + index
                                } else {
                                    selectedRecommendations - index
                                }
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = recommendation,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        if (selectedRecommendations.isNotEmpty()) {
            Button(
                onClick = {
                    recommendationsViewModel.deleteRecommendations(selectedRecommendations.toList())
                    selectedRecommendations = emptySet()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Удалить выбранные")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = { showDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        ) {
            Text("Добавить рекомендацию")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Новая рекомендация") },
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            text = {
                OutlinedTextField(
                    value = recommendationText,
                    onValueChange = { recommendationText = it },
                    label = { Text("Введите рекомендацию") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                    onClick = {
                        if (recommendationText.isNotBlank()) {
                            recommendationsViewModel.addRecommendation(recommendationText)
                            recommendationText = ""
                        }
                        showDialog = false
                    }
                ) {
                    Text("Добавить")
                }
            },
            dismissButton = {
                TextButton(
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    onClick = { showDialog = false }
                ) {
                    Text("Отмена")
                }
            }
        )
    }
}
