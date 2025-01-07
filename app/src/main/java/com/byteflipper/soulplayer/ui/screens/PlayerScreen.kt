//PlayerScreen.kt
package com.byteflipper.soulplayer.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.byteflipper.soulplayer.core.MusicPlayerCore
import com.byteflipper.soulplayer.core.ProgressManager
import com.byteflipper.soulplayer.ui.components.formatDuration
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.lifecycle.viewmodel.compose.viewModel
import com.byteflipper.soulplayer.viewmodel.AppViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(navController: NavHostController){
    val context = LocalContext.current
    val viewModel: AppViewModel = viewModel(factory = AppViewModel.AppViewModelFactory(context.applicationContext as android.app.Application))

    val musicPlayerCore = remember { MusicPlayerCore(context = context) }
    val progressManager = remember { ProgressManager(musicPlayerCore) }
    var currentProgress by remember { mutableStateOf(0f) }
    val isPlaying = remember{ mutableStateOf(false) }
    val currentTrack = remember { mutableStateOf(musicPlayerCore.playlistManager.getCurrentTrack()) }
    var showMenu by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()


    DisposableEffect(Unit) {
        onDispose {
            musicPlayerCore.release()
        }
    }
    LaunchedEffect(musicPlayerCore.isPlaying()) {
        isPlaying.value = musicPlayerCore.isPlaying()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(currentTrack.value?.title ?: "No track", color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {

                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            currentTrack.value?.cover?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Cover",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = currentTrack.value?.artist ?: "Unknown Artist", color = Color.White)
            Spacer(modifier = Modifier.height(32.dp))

            Slider(value = currentProgress, onValueChange = {
                currentProgress = it
                progressManager.seekTo(it)
            },
                onValueChangeFinished = {
                    isPlaying.value = musicPlayerCore.isPlaying()
                },
                modifier = Modifier.fillMaxWidth(0.9f),
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row (verticalAlignment = Alignment.CenterVertically){
                Text(
                    text = formatDuration(musicPlayerCore.getCurrentPosition()/1_000),
                    color = Color.White,
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatDuration(musicPlayerCore.getDuration()/1_000),
                    color = Color.White,
                )
            }


            Spacer(modifier = Modifier.height(16.dp))
            Row {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            musicPlayerCore.playlistManager.previousTrack()?.let{
                                musicPlayerCore.setMedia(it)
                                currentTrack.value = it
                            }
                        }
                    }
                ){
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            if(musicPlayerCore.isPlaying()){
                                musicPlayerCore.pause()
                                isPlaying.value = musicPlayerCore.isPlaying()
                            } else {
                                musicPlayerCore.play()
                                isPlaying.value = musicPlayerCore.isPlaying()
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (isPlaying.value) Icons.Filled.Clear else Icons.Filled.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            musicPlayerCore.playlistManager.nextTrack()?.let{
                                musicPlayerCore.setMedia(it)
                                currentTrack.value = it
                            }
                        }

                    }
                ){
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowRight,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        }
        LaunchedEffect(Unit) {
            while (true){
                currentProgress = progressManager.getProgress()
                kotlinx.coroutines.delay(500)
            }
        }
    }
}