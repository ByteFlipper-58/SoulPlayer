// SettingsScreen.kt

package com.byteflipper.soulplayer.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.byteflipper.soulplayer.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(){
    val context = LocalContext.current
    val viewModel: AppViewModel = viewModel(factory = AppViewModel.AppViewModelFactory(context.applicationContext as android.app.Application))
    val theme = viewModel.theme.collectAsState()
    val dynamicColor = viewModel.dynamicColor.collectAsState()
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Text("Theme Settings")
            Row (verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = theme.value == "system", onClick = { viewModel.setTheme("system") })
                Spacer(modifier = Modifier.width(4.dp))
                Text("System")
            }
            Row (verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = theme.value == "light", onClick = { viewModel.setTheme("light") })
                Spacer(modifier = Modifier.width(4.dp))
                Text("Light")
            }
            Row (verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = theme.value == "dark", onClick = { viewModel.setTheme("dark") })
                Spacer(modifier = Modifier.width(4.dp))
                Text("Dark")
            }
            Row (verticalAlignment = Alignment.CenterVertically) {
                Text("Dynamic Colors")
                Spacer(modifier = Modifier.width(4.dp))
                Switch(checked = dynamicColor.value, onCheckedChange = { viewModel.setDynamicColor(it)})
            }
        }
    }
}