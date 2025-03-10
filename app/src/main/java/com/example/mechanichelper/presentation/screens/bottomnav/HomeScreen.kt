package com.example.mechanichelper.presentation.screens.bottomnav

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mechanichelper.presentation.viewmodel.HomeViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(carName: String, initialMileage: String) {
    val viewModel = hiltViewModel<HomeViewModel>()
    val context = LocalContext.current

    val photoUri by viewModel.photoUri.collectAsState()
    val carMileage by viewModel.carMileage.collectAsState()
    val recommendation by viewModel.recommendation.collectAsState()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) viewModel.loadLastPhoto()
    }

    LaunchedEffect(photoUri) {
        photoUri?.let { uri ->
            if (uri.toString().contains("file")) {
                cameraLauncher.launch(uri)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) launchCamera(viewModel, cameraLauncher)
    }

    LaunchedEffect(Unit) {
        viewModel.updateMileage(initialMileage)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Обслуживание $carName") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            MaintenanceInfo(mileage = carMileage, recommendation = recommendation)

            Spacer(modifier = Modifier.height(24.dp))

            PhotoSection(
                photoUri = photoUri,
                onTakePhoto = {
                    checkCameraPermission(
                        context = context,
                        onGranted = { launchCamera(viewModel, cameraLauncher) },
                        onDenied = { permissionLauncher.launch(Manifest.permission.CAMERA) }
                    )
                }
            )
        }
    }
}

@Composable
private fun MaintenanceInfo(mileage: String, recommendation: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Текущий пробег: $mileage км",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Рекомендации: $recommendation",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun PhotoSection(
    photoUri: Uri?,
    onTakePhoto: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        photoUri?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = "Фото автомобиля",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Button(
            onClick = onTakePhoto,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Сделать фото")
        }
    }
}

private fun checkCameraPermission(
    context: Context,
    onGranted: () -> Unit,
    onDenied: () -> Unit
) {
    when {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> onGranted()

        else -> onDenied()
    }
}

private fun launchCamera(
    viewModel: HomeViewModel,
    launcher: androidx.activity.result.ActivityResultLauncher<Uri>
) {
    viewModel.takePhoto()
    viewModel.photoUri.value?.let { uri ->
        launcher.launch(uri)
    }
}