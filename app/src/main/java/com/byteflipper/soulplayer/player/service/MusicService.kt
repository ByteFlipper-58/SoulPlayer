package com.byteflipper.soulplayer.player.service

import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaSessionService() {

    @Inject
    lateinit var exoPlayer: ExoPlayer

    private var mediaSession: MediaSession? = null

    override fun onCreate() {
        super.onCreate()

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .setUsage(C.USAGE_MEDIA)
            .build()
        exoPlayer.setAudioAttributes(audioAttributes, true)

        // Создаем MediaSession
        mediaSession = MediaSession.Builder(this, exoPlayer).build()

        // TODO: Добавить NotificationManager для управления уведомлениями
        // TODO: Настроить MediaSession Callback (для обработки команд управления)
        // TODO: Настроить Foreground Service Notification
    }

    // Возвращаем MediaSession для связывания с контроллерами
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // TODO: Обработка команд запуска, например, для старта в foreground
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        mediaSession?.run {
            // Важно: Не освобождаем ExoPlayer здесь, так как он @Singleton и управляется Hilt.
            // Hilt позаботится о его жизненном цикле.
            // Если бы ExoPlayer создавался только для этого сервиса, его нужно было бы освобождать.
            // player.release()
            release() // Освобождаем только MediaSession
            mediaSession = null
        }
        super.onDestroy()
    }

    // TODO: Добавить логику для Foreground Service (startForeground, stopForeground)
    // TODO: Добавить обработку разрешений (FOREGROUND_SERVICE, POST_NOTIFICATIONS)
}
