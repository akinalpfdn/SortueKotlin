package com.akinalpfdn.sortue.utils

import android.content.Context
import android.media.MediaPlayer
import com.akinalpfdn.sortue.R

class AudioManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null

    fun playBackgroundMusic() {
        if (mediaPlayer == null) {
            // Assuming soundTrack.mp3 will be placed in res/raw/soundtrack.mp3
            // Note: Android resources must be lowercase.
            // I will need to verify if the user has this file or I should ask for it.
            // For now, I'll generate the code assuming the resource exists as R.raw.soundtrack
            try {
                // mediaPlayer = MediaPlayer.create(context, R.raw.soundtrack)
                // mediaPlayer?.isLooping = true
                // mediaPlayer?.setVolume(0.7f, 0.7f)
                // mediaPlayer?.start()
                
                // Placeholder until resource is migrated
                println("AudioManager: Background music would play here (R.raw.soundtrack)")
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
