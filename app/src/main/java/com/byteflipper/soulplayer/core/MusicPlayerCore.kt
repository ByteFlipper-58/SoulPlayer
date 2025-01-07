package com.byteflipper.soulplayer.core

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionToken
import android.content.ServiceConnection
import android.util.Log
import com.byteflipper.soulplayer.core.MusicPlaybackService
import java.util.concurrent.Executors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusicPlayerCore(private val context: Context) {
    private val sessionToken = SessionToken(context, ComponentName(context, MusicPlaybackService::class.java))
    private var mediaController: MediaController? = null
    private var serviceBound = false
    private val _isControllerReady = MutableStateFlow(false)
    val isControllerReady: StateFlow<Boolean> = _isControllerReady.asStateFlow()
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)


    private val controllerConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
            controllerFuture.addListener(
                {
                    try {
                        mediaController = controllerFuture.get()
                        serviceBound = true
                        _isControllerReady.value = true
                        Log.d("MusicPlayerCore", "Controller ready")
                    } catch (e: Exception) {
                        Log.e("MusicPlayerCore", "Error initializing controller", e)
                        _isControllerReady.value = false
                    }
                },
                Executors.newSingleThreadExecutor()
            )
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mediaController = null
            serviceBound = false
            _isControllerReady.value = false
            Log.d("MusicPlayerCore", "Service disconnected")
        }
    }

    init {
        val serviceIntent = Intent(context, MusicPlaybackService::class.java)
        context.startService(serviceIntent)
        context.bindService(serviceIntent, controllerConnection, Context.BIND_AUTO_CREATE)
    }

    val playlistManager = PlaylistManager()

    fun setMedia(track: MusicTrack) {
        try {
            val mediaItem = MediaItem.fromUri(track.data)
            mediaController?.run {
                setMediaItem(mediaItem)
                prepare()
            } ?: Log.e("MusicPlayerCore", "MediaController is null")
        } catch (e: Exception) {
            Log.e("MusicPlayerCore", "Error setting media", e)
        }
    }

    @JvmName("setPlaylistTracks")
    fun setPlaylist(tracks: List<MusicTrack>) {
        val uris = tracks.map { it.data }
        setPlaylist(uris)
    }

    @JvmName("setPlaylistUris")
    fun setPlaylist(uris: List<String>) {
        try {
            val mediaItems = uris.map { MediaItem.fromUri(it) }
            mediaController?.run {
                setMediaItems(mediaItems)
                prepare()
            } ?: Log.e("MusicPlayerCore", "MediaController is null")
        } catch (e: Exception) {
            Log.e("MusicPlayerCore", "Error setting playlist", e)
        }
    }

    fun play() {
        try {
            mediaController?.play() ?: Log.e("MusicPlayerCore", "MediaController is null")
        } catch (e: Exception) {
            Log.e("MusicPlayerCore", "Error playing", e)
        }
    }

    fun pause() {
        mediaController?.pause()
    }

    fun stop() {
        mediaController?.stop()
        playlistManager.nextTrack()?.let {
            coroutineScope.launch {
                setMedia(it)
            }
        }
    }

    fun seekTo(position: Long) {
        mediaController?.seekTo(position)
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

    fun isPlaying(): Boolean {
        return mediaController?.isPlaying ?: false
    }

    fun getCurrentPosition(): Long {
        return mediaController?.currentPosition ?: 0L
    }

    fun getDuration(): Long {
        return mediaController?.duration ?: 0L
    }
}