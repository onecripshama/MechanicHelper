package com.example.mechanichelper.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mechanichelper.presentation.screens.settings.DeveloperScreen
import com.example.mechanichelper.presentation.screens.auth.GreetingScreen
import com.example.mechanichelper.presentation.screens.auth.LoginScreen
import com.example.mechanichelper.presentation.screens.bottomnav.MainScreen
import com.example.mechanichelper.presentation.screens.settings.SettingsScreen
import com.example.mechanichelper.presentation.screens.auth.SignUpScreen
import com.example.mechanichelper.presentation.viewmodel.ThemeViewModel

@Composable
fun AppNavigation(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignUpScreen(navController) }
        composable("greeting") { GreetingScreen(navController) }
        composable("main/{carName}/{carMileage}") { backStackEntry ->
            val carName = backStackEntry.arguments?.getString("carName") ?: "Машина"
            val carMileage = backStackEntry.arguments?.getString("carMileage") ?: "0"
            MainScreen(mainNavController = navController, carName = carName, carMileage = carMileage)
        }
        composable("settings") {
            SettingsScreen(navController = navController, themeViewModel = themeViewModel)
        }
        composable("developer") { DeveloperScreen(navController = navController) }
    }
}