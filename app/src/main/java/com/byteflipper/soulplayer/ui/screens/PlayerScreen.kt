package com.byteflipper.soulplayer.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.byteflipper.soulplayer.core.MusicPlayerCore
import com.byteflipper.soulplayer.ui.components.formatDuration
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    navController: NavHostController,
    musicPlayerCore: MusicPlayerCore
) {
    val coroutineScope = rememberCoroutineScope()
    val isControllerReady by musicPlayerCore.isControllerReady.collectAsState()
    val isPlaying by musicPlayerCore.isPlaying.collectAsState()
    val currentPosition by musicPlayerCore.currentPosition.collectAsState()
    val duration by musicPlayerCore.duration.collectAsState()
    val currentTrack = remember { mutableStateOf(musicPlayerCore.playlistManager.getCurrentTrack()) }

    var sliderPosition by remember { mutableStateOf(0f) }
    var isSeeking by remember { mutableStateOf(false) }

    LaunchedEffect(currentPosition, duration) {
        if (!isSeeking && duration > 0) {
            sliderPosition = currentPosition.toFloat() / duration.toFloat()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(currentTrack.value?.title ?: "No track", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Album art
            currentTrack.value?.cover?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Album Art",
                    modifier = Modifier
                        .size(280.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Track info
            Text(
                text = currentTrack.value?.title ?: "No track",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = currentTrack.value?.artist ?: "Unknown Artist",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Seek bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Slider(
                    value = sliderPosition,
                    onValueChange = {
                        isSeeking = true
                        sliderPosition = it
                    },
                    onValueChangeFinished = {
                        isSeeking = false
                        if (duration > 0) {
                            musicPlayerCore.seekTo((sliderPosition * duration).toLong())
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatDuration(currentPosition),
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Text(
                        text = formatDuration(duration),
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Playback controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            musicPlayerCore.skipToPrevious()
                            currentTrack.value = musicPlayerCore.playlistManager.getCurrentTrack()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                IconButton(
                    onClick = {
                        if (isControllerReady) {
                            if (isPlaying) {
                                musicPlayerCore.pause()
                            } else {
                                musicPlayerCore.play()
                            }
                        }
                    },
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Clear else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            musicPlayerCore.skipToNext()
                            currentTrack.value = musicPlayerCore.playlistManager.getCurrentTrack()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}