package com.example.mechanichelper.presentation.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun GreetingScreen(navController: NavHostController) {
    var carName by remember { mutableStateOf("") }
    var carMileage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Добро пожаловать!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = carName,
            onValueChange = { carName = it },
            label = { Text("Название машины") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = carMileage,
            onValueChange = { carMileage = it },
            label = { Text("Проехал с последнего ТО (км)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("main/${carName}/${carMileage}")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Продолжить")
        }
    }
}