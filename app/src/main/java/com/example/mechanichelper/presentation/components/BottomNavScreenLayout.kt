package com.example.mechanichelper.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object BottomNavScreenDimens {
    val contentPadding = 16.dp
    val titleBottomSpacing = 16.dp
}

@Composable
fun BottomNavScreenLayout(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(BottomNavScreenDimens.contentPadding)
    ) {
        ScreenTitle(title = title)
        Spacer(modifier = Modifier.height(BottomNavScreenDimens.titleBottomSpacing))
        content()
    }
}
