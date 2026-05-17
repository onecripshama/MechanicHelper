package com.example.mechanichelper.presentation.screens.bottomnav

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mechanichelper.presentation.navigation.BottomNavScreen
import com.example.mechanichelper.presentation.navigation.BottomNavigationBar

@Composable
fun MainScreen(mainNavController: NavHostController) {
    val bottomNavController = rememberNavController()
    Scaffold(
        bottomBar = {
            val currentRoute = bottomNavController.currentBackStackEntry?.destination?.route
            if (currentRoute != "car_detail/{carId}") {
                BottomNavigationBar(navController = bottomNavController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = BottomNavScreen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavScreen.Home.route) {
                CarListScreen(
                    onCarClick = { carId ->
                        bottomNavController.navigate("car_detail/$carId")
                    }
                )
            }
            composable(
                route = "car_detail/{carId}",
                arguments = listOf(navArgument("carId") { type = NavType.StringType })
            ) {
                HomeScreen(onBack = { bottomNavController.popBackStack() })
            }
            composable(BottomNavScreen.Users.route) { UsersScreen() }
            composable(BottomNavScreen.Reminders.route) { RemindersScreen() }
            composable(BottomNavScreen.Profile.route) {
                ProfileScreen(
                    onSettingsClick = { mainNavController.navigate("settings") },
                    onDeveloperClick = { mainNavController.navigate("developer") }
                )
            }
        }
    }
}
