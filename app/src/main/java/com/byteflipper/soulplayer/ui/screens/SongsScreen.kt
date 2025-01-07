// app/src/main/java/com/byteflipper/soulplayer/ui/screens/SongsScreen.kt
package com.byteflipper.soulplayer.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.byteflipper.soulplayer.core.MusicScanner
import com.byteflipper.soulplayer.core.MusicTrack
import com.byteflipper.soulplayer.ui.components.SongGridItem
import com.byteflipper.soulplayer.ui.components.SongListItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.byteflipper.soulplayer.core.MusicPlayerCore
import com.byteflipper.soulplayer.navigation.Screen
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
@Composable
fun SongsScreen(navController: NavHostController) {
    val context = LocalContext.current
    var showMenu by remember { mutableStateOf(false) }
    var isListMode by remember { mutableStateOf(true) }
    val scanner = remember { MusicScanner(context) }
    val coroutineScope = rememberCoroutineScope()
    val tracks = remember { mutableStateListOf<MusicTrack>() }
    val musicPlayerCore = remember { MusicPlayerCore(context) }
    var navigateToPlayer by remember { mutableStateOf<MusicTrack?>(null) }
    var isControllerReady by remember{mutableStateOf(false)}


    // Set a callback for when the controller is ready
    musicPlayerCore.controllerReadyCallback = {
        isControllerReady = true
        Log.d("SongsScreen","Controller is ready callback")
        if(navigateToPlayer!= null){
            Log.d("SongsScreen", "Navigating to player with track: ${navigateToPlayer?.title}")
            musicPlayerCore.setMedia(navigateToPlayer!!)
            musicPlayerCore.playlistManager.addTrack(navigateToPlayer!!)
            navigateToPlayer = null
            navController.navigate(Screen.Player.route)
            Log.d("SongsScreen", "Navigation done")
        }
    }


    DisposableEffect(Unit) {
        coroutineScope.launch {
            scanMusicAndUpdateList(scanner, tracks)
        }
        onDispose {
            musicPlayerCore.release()
        }
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Songs", color = Color.White) },
                navigationIcon = {
                    Icon(imageVector = Icons.Filled.List, contentDescription = "Songs", tint = Color.White)
                },
                actions = {
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(imageVector = Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = {
                                showMenu = false
                                navController.navigate("settings")
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isListMode) "Grid Mode" else "List Mode") },
                            onClick = {
                                showMenu = false
                                isListMode = !isListMode
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (tracks.isEmpty()) {
                Text("No songs found")
            } else {
                AnimatedContent(targetState = isListMode,
                    transitionSpec = {
                        if (targetState) {
                            scaleIn() + fadeIn() togetherWith scaleOut() + fadeOut()
                        } else {
                            scaleIn() + fadeIn() togetherWith scaleOut() + fadeOut()
                        }.using(SizeTransform(clip = false))
                    }, label = "layout_animation"
                ) { targetState ->
                    if (targetState) {
                        SongList(tracks = tracks, onClick = { track ->
                            Log.d("SongsScreen", "Song clicked: ${track.title}, isControllerReady: $isControllerReady")
                            if (isControllerReady) {
                                musicPlayerCore.setMedia(track)
                                musicPlayerCore.playlistManager.addTrack(track)
                                navigateToPlayer = null
                                navController.navigate(Screen.Player.route)
                            }else{
                                navigateToPlayer = track
                            }
                        })
                    } else {
                        SongGrid(tracks = tracks, onClick = { track ->
                            Log.d("SongsScreen", "Song clicked: ${track.title}, isControllerReady: $isControllerReady")
                            if (isControllerReady) {
                                musicPlayerCore.setMedia(track)
                                musicPlayerCore.playlistManager.addTrack(track)
                                navigateToPlayer = null
                                navController.navigate(Screen.Player.route)
                            }else{
                                navigateToPlayer = track
                            }
                        })
                    }
                }
            }
        }
    }
}


@Composable
fun SongList(tracks: List<MusicTrack>, onClick: (MusicTrack) -> Unit) {
    LazyColumn {
        items(tracks) { track ->
            SongListItem(track = track, onClick = onClick)
        }
    }
}

@Composable
fun SongGrid(tracks: List<MusicTrack>, onClick: (MusicTrack) -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 180.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
    ) {
        items(tracks) { track ->
            SongGridItem(track = track, onClick = onClick)
        }
    }
}
private suspend fun scanMusicAndUpdateList(scanner: MusicScanner, tracks: MutableList<MusicTrack>) {
    val newTracks = withContext(Dispatchers.IO) {
        scanner.scanMusic()
    }
    tracks.clear()
    tracks.addAll(newTracks)
    Log.d("SongsScreen", "Found ${newTracks.size} tracks")
}