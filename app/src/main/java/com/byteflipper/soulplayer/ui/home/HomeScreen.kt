package com.byteflipper.soulplayer.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Домашний экран", // TODO: Использовать ресурсы строк
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Здесь будут рекомендации, недавно прослушанные и избранные треки.", // TODO: Использовать ресурсы строк
            style = MaterialTheme.typography.bodyMedium
        )
        // TODO: Реализовать секции с горизонтальным скроллом
    }
}
