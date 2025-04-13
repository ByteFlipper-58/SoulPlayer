package com.byteflipper.soulplayer.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.byteflipper.soulplayer.data.NavigationType
import com.byteflipper.soulplayer.presenter.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val currentNavigationType by viewModel.navigationType.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Настройки навигации", // TODO: Использовать ресурсы строк
            style = MaterialTheme.typography.titleLarge
        )

        NavigationTypeSelector(
            selectedType = currentNavigationType,
            onTypeSelected = { viewModel.setNavigationType(it) }
        )

    }
}

@Composable
fun NavigationTypeSelector(
    selectedType: NavigationType,
    onTypeSelected: (NavigationType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Тип панели навигации:", // TODO: Использовать ресурсы строк
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Нижняя панель", Modifier.weight(1f)) // TODO: Использовать ресурсы строк
            RadioButton(
                selected = selectedType == NavigationType.BOTTOM,
                onClick = { onTypeSelected(NavigationType.BOTTOM) }
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Вкладки", Modifier.weight(1f)) // TODO: Использовать ресурсы строк
            RadioButton(
                selected = selectedType == NavigationType.TABS,
                onClick = { onTypeSelected(NavigationType.TABS) }
            )
        }
    }
}
