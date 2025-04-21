package com.byteflipper.soulplayer.ui.nowplaying

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.byteflipper.soulplayer.R
import com.byteflipper.soulplayer.data.albumArtUri
import java.util.concurrent.TimeUnit

@Composable
fun NowPlayingScreen(
    viewModel: NowPlayingViewModel = hiltViewModel()
) {
    val playbackState by viewModel.playbackState.collectAsState()

    var sliderPosition by remember(playbackState.currentSong?.id, playbackState.currentPositionMs) {
        mutableFloatStateOf(playbackState.currentPositionMs.toFloat())
    }
    var isUserSeeking by remember { mutableStateOf(false) }

    LaunchedEffect(playbackState.isPlaying, playbackState.currentPositionMs) {
        if (!isUserSeeking) {
            sliderPosition = playbackState.currentPositionMs.toFloat()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Верхняя часть (можно добавить кнопку Назад или оставить пустым)
        Spacer(modifier = Modifier.height(16.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            // Обложка альбома
            AsyncImage(
                model = playbackState.currentSong?.albumArtUri,
                contentDescription = stringResource(R.string.album_art_content_description),
                placeholder = painterResource(id = R.drawable.album_24px), // Заглушка
                error = painterResource(id = R.drawable.album_24px), // Заглушка при ошибке
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Название песни
            Text(
                text = playbackState.currentSong?.title ?: stringResource(id = R.string.not_playing),
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))

            // Исполнитель
            Text(
                text = playbackState.currentSong?.artist ?: "-",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Слайдер прогресса
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = sliderPosition,
                    onValueChange = { newValue ->
                        isUserSeeking = true
                        sliderPosition = newValue
                    },
                    valueRange = 0f..(playbackState.totalDurationMs.toFloat().coerceAtLeast(1f)),
                    onValueChangeFinished = {
                        viewModel.seekTo(sliderPosition.toLong())
                        isUserSeeking = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(formatDuration(sliderPosition.toLong()), style = MaterialTheme.typography.labelSmall)
                    Text(formatDuration(playbackState.totalDurationMs), style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Кнопки управления
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Кнопка "Назад"
                IconButton(onClick = { viewModel.skipToPrevious() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.skip_previous_24px),
                        contentDescription = stringResource(R.string.action_previous),
                        modifier = Modifier.size(48.dp)
                    )
                }

                // Кнопка "Воспроизведение/Пауза"
                IconButton(onClick = { viewModel.playPause() }) {
                    Icon(
                        painter = painterResource(
                            id = if (playbackState.isPlaying) R.drawable.pause_24px else R.drawable.play_arrow_24px
                        ),
                        contentDescription = stringResource(if (playbackState.isPlaying) R.string.action_pause else R.string.action_play),
                        modifier = Modifier.size(64.dp)
                    )
                }

                // Кнопка "Вперед"
                IconButton(onClick = { viewModel.skipToNext() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.skip_next_24px),
                        contentDescription = stringResource(R.string.action_next),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}

// Вспомогательная функция для форматирования времени (ms в MM:SS)
private fun formatDuration(millis: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return String.format("%02d:%02d", minutes, seconds)
}
