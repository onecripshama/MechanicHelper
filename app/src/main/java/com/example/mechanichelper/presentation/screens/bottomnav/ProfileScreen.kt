package com.example.mechanichelper.presentation.screens.bottomnav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mechanichelper.R
import com.example.mechanichelper.presentation.components.BottomNavScreenLayout
import com.example.mechanichelper.presentation.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onSettingsClick: () -> Unit,
    onDeveloperClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()

    val displayLogin = profile.login.ifBlank { stringResource(R.string.profile_default_login) }

    BottomNavScreenLayout(title = stringResource(R.string.profile_title)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = stringResource(R.string.profile_avatar_cd),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(60.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = displayLogin,
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onSettingsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.profile_settings))
        }
        OutlinedButton(
            onClick = onDeveloperClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.profile_about_developer))
        }
        }
    }
}
