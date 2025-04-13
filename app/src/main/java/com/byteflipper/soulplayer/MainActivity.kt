package com.byteflipper.soulplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.byteflipper.soulplayer.ui.main.MainScreen
import com.byteflipper.soulplayer.ui.theme.SoulPlayerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SoulPlayerTheme {
                MainScreen()
            }
        }
    }
}