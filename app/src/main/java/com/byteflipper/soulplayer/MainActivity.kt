package com.byteflipper.soulplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.byteflipper.soulplayer.navigation.AppNavigation
import com.byteflipper.soulplayer.navigation.BottomNavigationBar
import com.byteflipper.soulplayer.ui.theme.SoulPlayerTheme
import com.byteflipper.soulplayer.viewmodel.AppViewModel
import com.byteflipper.soulplayer.core.MusicPlayerCore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: AppViewModel = viewModel(factory = AppViewModel.AppViewModelFactory(application))
            val theme by viewModel.theme.collectAsState()
            val dynamicColor by viewModel.dynamicColor.collectAsState()
            val context = LocalContext.current
            val musicPlayerCore = remember { MusicPlayerCore(context) }

            SoulPlayerTheme(
                dynamicColor = dynamicColor,
                darkTheme = when(theme){
                    "system" -> isSystemInDarkTheme()
                    "light" -> false
                    "dark" -> true
                    else -> false
                }
            ) {
                val navController = rememberNavController()
                val backgroundColor = MaterialTheme.colorScheme.background
                val animatedBackgroundColor by animateColorAsState(
                    targetValue = backgroundColor,
                    animationSpec = tween(durationMillis = 3000),
                    label = "background_animation",
                )
                Surface(modifier = Modifier.fillMaxSize(), color = animatedBackgroundColor) {
                    Scaffold(
                        bottomBar = { BottomNavigationBar(navController = navController) }
                    ) { innerPadding ->
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxSize()
                        ) {
                            AppNavigation(navController = navController, musicPlayerCore = musicPlayerCore)
                        }
                    }
                }
            }
        }
    }
}