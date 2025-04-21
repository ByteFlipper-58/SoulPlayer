package com.byteflipper.soulplayer.player.media3

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlaybackService : MediaSessionService() {

    @Inject
    lateinit var player: ExoPlayer // Внедряем ExoPlayer

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()
        // Создаем и инициализируем MediaSession
        mediaSession = MediaSession.Builder(this, player)
            .build()
    }

    // Возвращает MediaSession для подключения контроллеров
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    // Вызывается при уничтожении службы
    override fun onDestroy() {
        // Освобождаем ресурсы MediaSession
        mediaSession?.run {
            // Плеер освобождается отдельно, если он управляется Hilt как Singleton
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
