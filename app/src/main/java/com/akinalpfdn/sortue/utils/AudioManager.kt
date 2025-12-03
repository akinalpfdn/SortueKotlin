package com.akinalpfdn.sortue.utils

import android.content.Context
import android.media.MediaPlayer
import com.akinalpfdn.sortue.R

class AudioManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun playBackgroundMusic() {
        if (mediaPlayer == null) {
            try {
                mediaPlayer = MediaPlayer.create(context, R.raw.soundtrack)
                mediaPlayer?.isLooping = true
                mediaPlayer?.setVolume(0.7f, 0.7f)
                mediaPlayer?.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopBackgroundMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    companion object {
        @Volatile
        private var INSTANCE: AudioManager? = null

        fun getInstance(context: Context): AudioManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AudioManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
