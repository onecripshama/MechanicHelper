package com.example.mechanichelper.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavScreen(val route: String, val label: String, val icon: ImageVector) {
    data object Home : BottomNavScreen("home", "Главная", Icons.Filled.Home)
    data object Users : BottomNavScreen("users", "Клиенты", Icons.Filled.Search)
    data object Recommendations : BottomNavScreen("recommendations", "Рекомендации", Icons.Filled.Star)
    data object Profile : BottomNavScreen("profile", "Профиль", Icons.Filled.Person)
}
