package com.example.mechanichelper.presentation.screens.bottomnav

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import com.example.mechanichelper.domain.model.Car
import com.example.mechanichelper.presentation.components.BottomNavScreenLayout
import com.example.mechanichelper.presentation.viewmodel.CarListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarListScreen(
    onCarClick: (String) -> Unit,
    viewModel: CarListViewModel = hiltViewModel()
) {
    val cars by viewModel.cars.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    var showFormDialog by remember { mutableStateOf(false) }
    var editingCar by remember { mutableStateOf<Car?>(null) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingCar = null
                    showFormDialog = true
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Добавить автомобиль")
            }
        }
    ) { paddingValues ->
        BottomNavScreenLayout(
            title = "Мои автомобили",
            modifier = Modifier.padding(paddingValues)
        ) {
            if (cars.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Нет автомобилей.\nНажмите +, чтобы добавить.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cars, key = { it.car.id }) { item ->
                        CarListItemCard(
                            name = item.car.name,
                            mileage = item.car.mileage,
                            photoUri = item.photoUri,
                            onClick = { onCarClick(item.car.id) },
                            onEditClick = {
                                editingCar = item.car
                                showFormDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showFormDialog) {
        CarFormDialog(
            car = editingCar,
            onDismiss = { showFormDialog = false },
            onSave = { name, mileage ->
                if (editingCar != null) {
                    viewModel.updateCar(editingCar!!.id, name, mileage)
                } else {
                    viewModel.addCar(name, mileage)
                }
                showFormDialog = false
            },
            onDelete = editingCar?.let { car ->
                {
                    showFormDialog = false
                    editingCar = null
                    viewModel.deleteCar(car.id)
                }
            }
        )
    }
}

@Composable
private fun CarFormDialog(
    car: Car?,
    onDismiss: () -> Unit,
    onSave: (name: String, mileage: Int) -> Unit,
    onDelete: (() -> Unit)?
) {
    var name by remember(car) { mutableStateOf(car?.name ?: "") }
    var mileage by remember(car) { mutableStateOf(car?.mileage?.toString() ?: "") }
    var error by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (car == null) "Добавить автомобиль" else "Редактировать автомобиль",
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; error = null },
                    label = { Text("Название") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = mileage,
                    onValueChange = { mileage = it; error = null },
                    label = { Text("Пробег (км)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Отмена")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val trimmedName = name.trim()
                            val mileageValue = mileage.trim().toIntOrNull()
                            when {
                                trimmedName.isBlank() -> error = "Введите название"
                                mileageValue == null || mileageValue < 0 ->
                                    error = "Введите корректный пробег"
                                else -> onSave(trimmedName, mileageValue)
                            }
                        }
                    ) {
                        Text("Сохранить")
                    }
                }

                if (onDelete != null) {
                    TextButton(
                        onClick = onDelete,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Удалить автомобиль")
                    }
                }
            }
        }
    }
}

@Composable
private fun CarListItemCard(
    name: String,
    mileage: Int,
    photoUri: String?,
    onClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (photoUri != null) {
                AsyncImage(
                    model = photoUri,
                    contentDescription = name,
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.size(72.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = name.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$mileage км",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Редактировать"
                )
            }
        }
    }
}
