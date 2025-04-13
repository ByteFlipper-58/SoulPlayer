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
import com.byteflipper.soulplayer.ui.albumdetails.AlbumDetailsScreen
import com.byteflipper.soulplayer.ui.albums.AlbumsScreen
import com.byteflipper.soulplayer.ui.artistdetails.ArtistDetailsScreen
import com.byteflipper.soulplayer.ui.artists.ArtistsScreen
import com.byteflipper.soulplayer.ui.home.HomeScreen
import com.byteflipper.soulplayer.ui.library.LibraryScreen
import com.byteflipper.soulplayer.ui.nowplaying.NowPlayingScreen
import com.byteflipper.soulplayer.ui.playlistdetails.PlaylistDetailsScreen
import com.byteflipper.soulplayer.ui.playlists.PlaylistsScreen
import com.byteflipper.soulplayer.ui.search.SearchScreen
import com.byteflipper.soulplayer.ui.settings.SettingsScreen
import com.byteflipper.soulplayer.ui.songs.SongsScreen


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
        composable(AppScreens.Library.route) { LibraryScreen() }
        composable(AppScreens.Songs.route) { SongsScreen() }
        composable(AppScreens.Artists.route) { ArtistsScreen() }
        composable(AppScreens.Albums.route) { AlbumsScreen() }
        composable(AppScreens.Playlists.route) { PlaylistsScreen() }
        composable(AppScreens.Search.route) { SearchScreen(navController) }
        composable(AppScreens.Settings.route) { SettingsScreen(navController) }
        composable(AppScreens.NowPlaying.route) { NowPlayingScreen() }

        composable(
            route = AppScreens.ArtistDetails.route,
            arguments = AppScreens.ArtistDetails.arguments
        ) { backStackEntry ->
            val artistId = backStackEntry.arguments?.getString(AppScreens.ArtistDetails.ARG_ID)
            ArtistDetailsScreen(artistId = artistId)
        }
        composable(
            route = AppScreens.AlbumDetails.route,
            arguments = AppScreens.AlbumDetails.arguments
        ) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getString(AppScreens.AlbumDetails.ARG_ID)
            AlbumDetailsScreen(albumId = albumId)
        }
        composable(
            route = AppScreens.PlaylistDetails.route,
            arguments = AppScreens.PlaylistDetails.arguments
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getString(AppScreens.PlaylistDetails.ARG_ID)
            PlaylistDetailsScreen(playlistId = playlistId)
        }
    }
}