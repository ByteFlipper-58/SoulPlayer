// MusicContentObserver.kt
package com.byteflipper.soulplayer.core

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore

class MusicContentObserver(
    private val context: Context,
    private val onChange: () -> Unit
) : ContentObserver(Handler(Looper.getMainLooper())) {

    private val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        onChange.invoke()
    }

    fun startWatching() {
        context.contentResolver.registerContentObserver(uri, true, this)
    }
    fun stopWatching(){
        context.contentResolver.unregisterContentObserver(this)
    }
}