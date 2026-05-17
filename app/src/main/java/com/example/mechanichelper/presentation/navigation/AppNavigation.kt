package com.example.mechanichelper.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mechanichelper.presentation.screens.settings.DeveloperScreen
import com.example.mechanichelper.presentation.screens.auth.LoginScreen
import com.example.mechanichelper.presentation.screens.bottomnav.MainScreen
import com.example.mechanichelper.presentation.screens.settings.SettingsScreen
import com.example.mechanichelper.presentation.screens.auth.SignUpScreen
import com.example.mechanichelper.presentation.viewmodel.AuthViewModel
import com.example.mechanichelper.presentation.viewmodel.SettingsViewModel

@Composable
fun AppNavigation(settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "main" else "login"
    ) {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("main") { MainScreen(mainNavController = navController) }
        composable("settings") {
            SettingsScreen(navController = navController, settingsViewModel = settingsViewModel)
        }
        composable("developer") { DeveloperScreen(navController = navController) }
    }
}
