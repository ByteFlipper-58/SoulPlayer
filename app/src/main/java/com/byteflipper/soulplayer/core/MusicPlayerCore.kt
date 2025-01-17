package com.byteflipper.soulplayer.core

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.byteflipper.soulplayer.core.MusicPlaybackService
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MusicPlayerCore(private val context: Context) {
    private val sessionToken = SessionToken(context, ComponentName(context, MusicPlaybackService::class.java))
    private var mediaController: MediaController? = null
    private var serviceBound = false
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
    val playlistManager = PlaylistManager()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private val _isControllerReady = MutableStateFlow(false)
    val isControllerReady: StateFlow<Boolean> = _isControllerReady

    private val controllerListener = object : Player.Listener {
        override fun onIsPlayingChanged(playing: Boolean) {
            _isPlaying.value = playing
        }

        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) {
            updatePosition()
        }
    }

    private val controllerConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("MusicPlayerCore", "Service Connected: $name")
            val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
            controllerFuture.addListener({
                try {
                    mediaController = controllerFuture.get().apply {
                        addListener(controllerListener)
                    }
                    serviceBound = true
                    _isControllerReady.value = true
                    startPositionUpdates()
                    Log.d("MusicPlayerCore", "Controller ready")
                } catch (e: Exception) {
                    Log.e("MusicPlayerCore", "Error initializing controller", e)
                }
            }, Executors.newSingleThreadExecutor())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mediaController = null
            serviceBound = false
            _isControllerReady.value = false
        }
    }

    init {
        val serviceIntent = Intent(context, MusicPlaybackService::class.java)
        context.startService(serviceIntent)
        context.bindService(serviceIntent, controllerConnection, Context.BIND_AUTO_CREATE)
    }

    private fun startPositionUpdates() {
        coroutineScope.launch {
            while (true) {
                updatePosition()
                kotlinx.coroutines.delay(500)
            }
        }
    }

    private fun updatePosition() {
        mediaController?.let { controller ->
            _currentPosition.value = controller.currentPosition
            _duration.value = controller.duration
        }
    }

    fun setMedia(track: MusicTrack) {
        if (!_isControllerReady.value) {
            Log.e("MusicPlayerCore", "MediaController is not ready when setMedia is called")
            return
        }

        try {
            val mediaItem = MediaItem.fromUri(track.data)
            mediaController?.run {
                setMediaItem(mediaItem)
                prepare()
            }
            playlistManager.clearPlaylist()
            playlistManager.addTrack(track)
        } catch (e: Exception) {
            Log.e("MusicPlayerCore", "Error setting media", e)
        }
    }

    fun setPlaylistTracks(tracks: List<MusicTrack>) {
        playlistManager.clearPlaylist()
        playlistManager.addTracks(tracks)
        val mediaItems = tracks.map { MediaItem.fromUri(it.data) }
        mediaController?.run {
            setMediaItems(mediaItems)
            prepare()
        }
    }

    fun play() {
        mediaController?.play()
    }

    fun pause() {
        mediaController?.pause()
    }

    fun stop() {
        mediaController?.stop()
        mediaController?.clearMediaItems()
    }

    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    fun skipToNext() {
        playlistManager.nextTrack()?.let { track ->
            setMedia(track)
            play()
        }
    }

    fun skipToPrevious() {
        playlistManager.previousTrack()?.let { track ->
            setMedia(track)
            play()
        }
    }

    fun release() {
        if (serviceBound) {
            try {
                context.unbindService(controllerConnection)
            } catch (e: Exception) {
                Log.e("MusicPlayerCore", "Error unbinding service", e)
            }
            serviceBound = false
        }
        mediaController?.release()
        mediaController = null
        playlistManager.clearPlaylist()
    }
}