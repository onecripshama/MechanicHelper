package com.example.mechanichelper.presentation.screens.bottomnav

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mechanichelper.presentation.navigation.BottomNavScreen
import com.example.mechanichelper.presentation.navigation.BottomNavigationBar


@Composable
fun MainScreen(mainNavController: NavHostController, carName: String, carMileage: String) {
    val bottomNavController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = bottomNavController) }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavScreen.Home.route) { HomeScreen(carName, carMileage) }
            composable(BottomNavScreen.Users.route) { UsersScreen() }
            composable(BottomNavScreen.Recommendations.route) { RecommendationsScreen() }
            composable(BottomNavScreen.Profile.route) {
                ProfileScreen(
                    onSettingsClick = { mainNavController.navigate("settings") },
                    onDeveloperClick = { mainNavController.navigate("developer") }
                )
            }
        }
    }
}