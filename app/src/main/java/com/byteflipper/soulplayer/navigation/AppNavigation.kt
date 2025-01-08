package com.byteflipper.soulplayer.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.byteflipper.soulplayer.ui.screens.DashboardScreen
import com.byteflipper.soulplayer.ui.screens.PlayerScreen
import com.byteflipper.soulplayer.ui.screens.SongsScreen
import com.byteflipper.soulplayer.core.MusicPlayerCore
import com.byteflipper.soulplayer.ui.screens.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object Songs : Screen("songs")
    object Settings : Screen("settings")
    object Player : Screen("player")
}

@Composable
fun AppNavigation(navController: NavHostController, musicPlayerCore: MusicPlayerCore) {
    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
        composable(Screen.Songs.route) {
            SongsScreen(navController = navController, musicPlayerCore = musicPlayerCore)
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
        composable(Screen.Player.route) {
            PlayerScreen(navController = navController, musicPlayerCore = musicPlayerCore)
        }
    }
}