package com.byteflipper.soulplayer.core

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.byteflipper.soulplayer.core.MusicPlaybackService
import java.util.concurrent.Executors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusicPlayerCore(private val context: Context) {
    private val sessionToken = SessionToken(context, ComponentName(context, MusicPlaybackService::class.java))
    private var mediaController: MediaController? = null
    private var serviceBound = false
    var controllerReadyCallback: (() -> Unit)? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
    val playlistManager = PlaylistManager()
    private var isControllerReady = false

    private val controllerConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("MusicPlayerCore", "Service Connected: $name")
            val controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
            Log.d("MusicPlayerCore", "MediaController.Builder created")
            controllerFuture.addListener({
                try {
                    mediaController = controllerFuture.get()
                    serviceBound = true
                    isControllerReady = true
                    Log.d("MusicPlayerCore", "Controller ready")
                    controllerReadyCallback?.invoke()
                } catch (e: Exception) {
                    Log.e("MusicPlayerCore", "Error initializing controller", e)
                }
            }, Executors.newSingleThreadExecutor())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.d("MusicPlayerCore", "Service Disconnected: $name") // First Breakpoint
            mediaController = null
            serviceBound = false
            Log.d("MusicPlayerCore", "Service Disconnected: serviceBound set to $serviceBound") //Second Breakpoint

        }
    }
    init {
        val serviceIntent = Intent(context, MusicPlaybackService::class.java)
        context.startService(serviceIntent)
        context.bindService(serviceIntent, controllerConnection, Context.BIND_AUTO_CREATE)
        Log.d("MusicPlayerCore", "MusicPlayerCore initialized, service started and bound")
    }
    fun setMedia(track: MusicTrack) {
        Log.d("MusicPlayerCore", "Setting media for track: ${track.title}")
        if (!isControllerReady) {
            Log.e("MusicPlayerCore", "MediaController is not ready when setMedia is called")
            return
        }
        if (mediaController == null) {
            Log.e("MusicPlayerCore", "MediaController is null when setMedia is called")
            return
        }
        try {
            val mediaItem = MediaItem.fromUri(track.data)
            mediaController?.run {
                setMediaItem(mediaItem)
                prepare()
            }
        } catch (e: Exception) {
            Log.e("MusicPlayerCore", "Error setting media", e)
        }
    }

    fun setPlaylistTracks(tracks: List<MusicTrack>) {
        val uris = tracks.map { it.data }
        setPlaylistUris(uris)
    }

    fun setPlaylistUris(uris: List<String>) {
        Log.d("MusicPlayerCore", "Setting playlist with uris: $uris")
        if (!isControllerReady) {
            Log.e("MusicPlayerCore", "MediaController is not ready when setPlaylist is called")
            return
        }
        if (mediaController == null) {
            Log.e("MusicPlayerCore", "MediaController is null when setPlaylist is called")
            return
        }
        try {
            val mediaItems = uris.map { MediaItem.fromUri(it) }
            mediaController?.run {
                setMediaItems(mediaItems)
                prepare()
            }
        } catch (e: Exception) {
            Log.e("MusicPlayerCore", "Error setting playlist", e)
        }
    }

    fun play() {
        if (!isControllerReady) {
            Log.e("MusicPlayerCore", "MediaController is not ready when play is called")
            return
        }
        if (mediaController == null) {
            Log.e("MusicPlayerCore", "MediaController is null when play is called")
            return
        }
        try {
            mediaController?.play()
            Log.d("MusicPlayerCore", "Playing")
        } catch (e: Exception) {
            Log.e("MusicPlayerCore", "Error playing", e)
        }
    }

    fun pause() {
        if (!isControllerReady) {
            Log.e("MusicPlayerCore", "MediaController is not ready when pause is called")
            return
        }
        if (mediaController == null) {
            Log.e("MusicPlayerCore", "MediaController is null when pause is called")
            return
        }
        mediaController?.pause()
        Log.d("MusicPlayerCore", "Paused")
    }

    fun stop() {
        if (!isControllerReady) {
            Log.e("MusicPlayerCore", "MediaController is not ready when stop is called")
            return
        }
        if (mediaController == null) {
            Log.e("MusicPlayerCore", "MediaController is null when stop is called")
            return
        }
        mediaController?.stop()
        Log.d("MusicPlayerCore", "Stopped")
        playlistManager.nextTrack()?.let {
            coroutineScope.launch {
                setMedia(it)
            }
        }
    }


    fun seekTo(position: Long) {
        if (!isControllerReady) {
            Log.e("MusicPlayerCore", "MediaController is not ready when seekTo is called")
            return
        }
        if (mediaController == null) {
            Log.e("MusicPlayerCore", "MediaController is null when seekTo is called")
            return
        }
        mediaController?.seekTo(position)
        Log.d("MusicPlayerCore", "Seeking to position: $position")
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
        Log.d("MusicPlayerCore", "Released, service unbound")
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