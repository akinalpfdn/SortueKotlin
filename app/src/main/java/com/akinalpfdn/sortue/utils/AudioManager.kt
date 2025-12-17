package com.akinalpfdn.sortue.utils

import android.content.Context
import android.media.MediaPlayer
import com.akinalpfdn.sortue.R

class AudioManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private val prefs = context.getSharedPreferences("sortue_prefs", Context.MODE_PRIVATE)

    var isMusicEnabled: Boolean
        get() = prefs.getBoolean("music_enabled", true)
        set(value) {
            prefs.edit().putBoolean("music_enabled", value).apply()
            if (value) {
                playBackgroundMusic()
            } else {
                pauseBackgroundMusic()
            }
        }
    
    var musicVolume: Float
        get() = prefs.getFloat("music_volume", 0.7f)
        set(value) {
            prefs.edit().putFloat("music_volume", value).apply()
            mediaPlayer?.setVolume(value, value)
        }

    fun playBackgroundMusic() {
        if (!isMusicEnabled) return

        if (mediaPlayer == null) {
            try {
                mediaPlayer = MediaPlayer.create(context, R.raw.soundtrack)
                mediaPlayer?.isLooping = true
                mediaPlayer?.setVolume(musicVolume, musicVolume)
                
                // Robustness: Handle completion manually if looping fails on some devices
                mediaPlayer?.setOnCompletionListener { 
                    try {
                        it.start() 
                    } catch (e: Exception) { e.printStackTrace() }
                }

                // Robustness: Handle errors by resetting
                mediaPlayer?.setOnErrorListener { mp, _, _ ->
                    mp.reset()
                    // Re-create next time play is called
                    mediaPlayer = null 
                    playBackgroundMusic()
                    true
                }

                mediaPlayer?.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    fun pauseBackgroundMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    fun resumeBackgroundMusic() {
        if (isMusicEnabled && mediaPlayer != null && mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
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
