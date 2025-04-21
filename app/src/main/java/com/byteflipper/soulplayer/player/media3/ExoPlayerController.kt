package com.byteflipper.soulplayer.player.media3

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.byteflipper.soulplayer.data.Song
import com.byteflipper.soulplayer.player.PlaybackState
import com.byteflipper.soulplayer.player.PlayerController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExoPlayerController @Inject constructor(
    @ApplicationContext private val context: Context,
    private val exoPlayer: ExoPlayer
) : PlayerController {

    // MediaController для связи с MediaSessionService
    private var mediaController: MediaController? = null

    private val scope = CoroutineScope(Dispatchers.Main + Job())

    private val _playbackState = MutableStateFlow(PlaybackState())
    override val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var positionUpdateJob: Job? = null
    private var currentPlaylist: List<Song> = emptyList()

    init {
        setupPlayerListener()
        initializeMediaController()
    }

    private fun initializeMediaController() {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                mediaController = controllerFuture.get()
                // TODO Добавить слушателя к mediaController, чтобы получать обновления из сессии,
                // но пока мы используем прямой слушатель ExoPlayer
            },
            MoreExecutors.directExecutor() // Выполняем в том же потоке
        )
    }

    private fun setupPlayerListener() {
        exoPlayer.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playbackState.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) {
                    startPositionUpdates()
                } else {
                    stopPositionUpdates()
                }
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                mediaItem?.let { currentMediaItem ->
                    val song = findSongByMediaId(currentMediaItem.mediaId)
                    _playbackState.update {
                        it.copy(
                            currentSong = song,
                            totalDurationMs = exoPlayer.duration.coerceAtLeast(0L),
                            currentPositionMs = 0L
                        )
                    }
                    // Перезапускаем обновление позиции, т.к. длительность могла измениться
                    if (_playbackState.value.isPlaying) {
                        startPositionUpdates()
                    }
                } ?: run {
                    // Если mediaItem null (например, плейлист закончился)
                    _playbackState.update {
                        it.copy(
                            currentSong = null,
                            isPlaying = false,
                            currentPositionMs = 0L,
                            totalDurationMs = 0L
                        )
                    }
                    stopPositionUpdates()
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                // Можно обрабатывать состояния BUFFERING, ENDED, IDLE
                if (playbackState == Player.STATE_READY) {
                    // Обновляем длительность, когда плеер готов
                    _playbackState.update {
                        it.copy(totalDurationMs = exoPlayer.duration.coerceAtLeast(0L))
                    }
                    // Запускаем обновление позиции, если уже играет
                    if (_playbackState.value.isPlaying) {
                        startPositionUpdates()
                    }
                } else if (playbackState == Player.STATE_ENDED) {
                    _playbackState.update {
                        it.copy(
                            isPlaying = false,
                            currentPositionMs = it.totalDurationMs
                        )
                    } // Показываем конец трека
                    stopPositionUpdates()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                _playbackState.update { it.copy(isPlaying = false) } // Останавливаем воспроизведение при ошибке
                stopPositionUpdates()
                // TODO: Добавить логирование или показ ошибки пользователю
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                // Обновляем позицию при ручной перемотке или авто-переходе
                _playbackState.update { it.copy(currentPositionMs = newPosition.positionMs) }
            }
        })
    }

    private fun startPositionUpdates() {
        stopPositionUpdates() // Останавливаем предыдущий job, если он был
        positionUpdateJob = scope.launch {
            while (isActive && _playbackState.value.isPlaying) {
                _playbackState.update {
                    it.copy(currentPositionMs = exoPlayer.currentPosition.coerceAtLeast(0L))
                }
                delay(100) // Обновляем позицию ~10 раза в секунду
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    override suspend fun play(song: Song?) {
        if (song != null) {
            // Если передана конкретная песня, ищем ее в текущем плейлисте или создаем новый из одной песни
            val playlistIndex = currentPlaylist.indexOfFirst { it.id == song.id }
            if (playlistIndex != -1) {
                // Песня найдена в текущем плейлисте
                if (exoPlayer.currentMediaItemIndex != playlistIndex || !exoPlayer.isPlaying) {
                    exoPlayer.seekToDefaultPosition(playlistIndex)
                    exoPlayer.prepare() // Нужно подготовить, если плеер был остановлен
                    exoPlayer.play()
                }
            } else {
                // Песни нет в текущем плейлисте, создаем новый из одной песни
                setPlaylist(listOf(song), startPlaying = true)
            }
        } else {
            // Если песня не передана, просто возобновляем воспроизведение
            if (exoPlayer.playbackState == Player.STATE_READY || exoPlayer.playbackState == Player.STATE_ENDED) {
                exoPlayer.play()
            } else if (exoPlayer.playbackState == Player.STATE_IDLE && exoPlayer.mediaItemCount > 0) {
                // Если плеер был остановлен, но плейлист есть
                exoPlayer.prepare()
                exoPlayer.play()
            }
        }
    }


    override suspend fun pause() {
        exoPlayer.pause()
    }

    override suspend fun next() {
        if (exoPlayer.hasNextMediaItem()) {
            exoPlayer.seekToNextMediaItem()
        }
    }

    override suspend fun previous() {
        if (exoPlayer.hasPreviousMediaItem()) {
            exoPlayer.seekToPreviousMediaItem()
        } else {
            // Если нет предыдущего трека, перематываем на начало текущего
            seekTo(0)
        }
    }

    override suspend fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs.coerceIn(0, exoPlayer.duration.coerceAtLeast(0L)))
        // Обновляем стейт немедленно для лучшего UX
        _playbackState.update {
            it.copy(
                currentPositionMs = exoPlayer.currentPosition.coerceAtLeast(
                    0L
                )
            )
        }
    }

    override suspend fun setPlaylist(songs: List<Song>, startPlaying: Boolean) {
        currentPlaylist = songs
        // Фильтруем песни с null contentUri и создаем MediaItem
        val mediaItems = songs.mapNotNull { song ->
            song.contentUri?.let { uri -> // Используем contentUri и проверяем на null
                MediaItem.Builder()
                    .setUri(uri) // Используем contentUri
                    .setMediaId(song.id.toString()) // Конвертируем Long ID в String
                    .setMediaMetadata(
                        MediaMetadata.Builder()
                            .setTitle(song.title)
                            .setArtist(song.artist)
                            // TODO Добавить другие метаданные
                            .build()
                    )
                    .build()
            }
        }

        exoPlayer.setMediaItems(
            mediaItems,
            !startPlaying
        ) // resetPosition = true если не начинаем играть сразу
        exoPlayer.prepare()
        if (startPlaying) {
                exoPlayer.play()
            } else {
                // Обновляем информацию о первом треке, даже если не играем
                songs.firstOrNull()?.let { firstSong ->
                    _playbackState.update {
                        it.copy(
                            currentSong = firstSong,
                            totalDurationMs = 0, // Длительность будет известна после prepare/play
                            currentPositionMs = 0
                        )
                    }
                }
            }
    }

    override fun release() {
        stopPositionUpdates()
        scope.coroutineContext[Job]?.cancel()
        mediaController?.release()
        mediaController = null
    }

    // Вспомогательная функция для поиска песни по MediaId
    private fun findSongByMediaId(mediaId: String?): Song? {
        if (mediaId == null) return null
        return currentPlaylist.find { it.id.toString() == mediaId }
    }
}
