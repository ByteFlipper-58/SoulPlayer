package com.byteflipper.soulplayer.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.byteflipper.soulplayer.ui.home.HomeScreen
import com.byteflipper.soulplayer.ui.settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = AppScreens.Home.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(AppScreens.Home.route) { HomeScreen(navController) }
        composable(AppScreens.Songs.route) { SongsScreenPlaceholder(navController) }
        composable(AppScreens.Artists.route) { ArtistsScreenPlaceholder(navController) }
        composable(AppScreens.Albums.route) { AlbumsScreenPlaceholder(navController) }
        composable(AppScreens.Playlists.route) { PlaylistsScreenPlaceholder(navController) }
        composable(AppScreens.Search.route) { com.byteflipper.soulplayer.ui.search.SearchScreen(navController) }
        composable(AppScreens.Settings.route) { SettingsScreen(navController) }
        composable(AppScreens.NowPlaying.route) { NowPlayingScreenPlaceholder(navController) }

        composable(
            route = AppScreens.ArtistDetails.route,
            arguments = AppScreens.ArtistDetails.arguments
        ) { backStackEntry ->
            val artistId = backStackEntry.arguments?.getString(AppScreens.ArtistDetails.ARG_ID)
            ArtistDetailsScreenPlaceholder(navController, artistId)
        }
        composable(
            route = AppScreens.AlbumDetails.route,
            arguments = AppScreens.AlbumDetails.arguments
        ) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getString(AppScreens.AlbumDetails.ARG_ID)
            AlbumDetailsScreenPlaceholder(navController, albumId)
        }
        composable(
            route = AppScreens.PlaylistDetails.route,
            arguments = AppScreens.PlaylistDetails.arguments
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getString(AppScreens.PlaylistDetails.ARG_ID)
            PlaylistDetailsScreenPlaceholder(navController, playlistId)
        }
    }
}

// --- Заглушки для экранов ---
// В будущем их нужно будет вынести в отдельные файлы/модули

@Composable
fun PlaceholderScreen(screenName: String, navController: NavHostController, id: String? = null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "$screenName ${id?.let { "(ID: $it)" } ?: ""}")
    }
    // TODO: Реализовать реальные экраны
}

@Composable fun SongsScreenPlaceholder(navController: NavHostController) = PlaceholderScreen("Songs", navController)
@Composable fun ArtistsScreenPlaceholder(navController: NavHostController) = PlaceholderScreen("Artists", navController)
@Composable fun AlbumsScreenPlaceholder(navController: NavHostController) = PlaceholderScreen("Albums", navController)
@Composable fun PlaylistsScreenPlaceholder(navController: NavHostController) = PlaceholderScreen("Playlists", navController)
@Composable fun NowPlayingScreenPlaceholder(navController: NavHostController) = PlaceholderScreen("Now Playing", navController)
@Composable fun ArtistDetailsScreenPlaceholder(navController: NavHostController, artistId: String?) = PlaceholderScreen("Artist Details", navController, artistId)
@Composable fun AlbumDetailsScreenPlaceholder(navController: NavHostController, albumId: String?) = PlaceholderScreen("Album Details", navController, albumId)
@Composable fun PlaylistDetailsScreenPlaceholder(navController: NavHostController, playlistId: String?) = PlaceholderScreen("Playlist Details", navController, playlistId)
