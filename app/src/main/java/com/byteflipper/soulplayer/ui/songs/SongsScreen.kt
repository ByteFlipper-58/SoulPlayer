package com.byteflipper.soulplayer.ui.songs

import android.Manifest
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip // Для скругления углов
import androidx.compose.ui.layout.ContentScale // Для масштабирования изображения
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource // Для изображения-заглушки
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage // Для загрузки изображений
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController // Импортируем NavHostController
import com.byteflipper.soulplayer.R
import com.byteflipper.soulplayer.data.Song // Используем Song из app.data
import com.byteflipper.soulplayer.data.albumArtUri // Импортируем расширение для URI обложки
import com.byteflipper.soulplayer.navigation.AppScreens // Импортируем AppScreens для маршрута
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SongsScreen(
    navController: NavHostController, // Добавляем NavController
    viewModel: SongsViewModel = hiltViewModel()
) {
    val songs by viewModel.songs.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // --- Запрос разрешений ---
    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val permissionState = rememberPermissionState(permission)

    LaunchedEffect(permissionState) {
        if (!permissionState.status.isGranted && !permissionState.status.shouldShowRationale) {
            permissionState.launchPermissionRequest()
        }
    }

    // --- Отображение контента ---
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            // Показываем запрос разрешения, если оно не выдано
            !permissionState.status.isGranted -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Требуется разрешение на чтение аудиофайлов.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = { permissionState.launchPermissionRequest() }) {
                        Text("Запросить разрешение")
                    }
                }
            }
            // Показываем индикатор загрузки
            isLoading -> {
                CircularProgressIndicator()
            }
            // Показываем список песен
            songs.isNotEmpty() -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(songs.size, key = { index -> songs[index].id }) { index ->
                        val song = songs[index]
                        SongItem(
                            song = song,
                            onClick = {
                                viewModel.playSong(index) // Вызываем playSong с индексом
                                navController.navigate(AppScreens.NowPlaying.route) // Переходим на экран NowPlaying
                            }
                        )
                        Divider() // Добавляем разделитель между элементами
                    }
                }
            }
            // Показываем сообщение, если список пуст
            else -> {
                Text(text = stringResource(id = R.string.no_songs_found))
            }
        }
    }

    // Перезагружаем песни, если разрешение было предоставлено
    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted) {
            viewModel.loadSongs()
        }
    }
}

@Composable
fun SongItem(song: Song, onClick: () -> Unit) { // Используем Song напрямую
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Отображаем обложку альбома
        AsyncImage(
            model = song.albumArtUri,
            contentDescription = stringResource(R.string.album_art_content_description), // Добавим строку позже
            placeholder = painterResource(id = R.drawable.album_24px), // Заглушка по умолчанию
            error = painterResource(id = R.drawable.album_24px), // Заглушка при ошибке
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(MaterialTheme.shapes.small) // Скругляем углы
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
        // TODO: Добавить меню опций (три точки)
    }
}
