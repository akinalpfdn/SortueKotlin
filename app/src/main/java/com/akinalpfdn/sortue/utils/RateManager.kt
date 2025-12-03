package com.akinalpfdn.sortue.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RateManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("sortue_prefs", Context.MODE_PRIVATE)
    
    private var launchCount: Int
        get() = prefs.getInt("app_launch_count", 0)
        set(value) = prefs.edit().putInt("app_launch_count", value).apply()

    private var nextReviewThreshold: Int
        get() = prefs.getInt("next_review_threshold", 10)
        set(value) = prefs.edit().putInt("next_review_threshold", value).apply()

    private val _showRatePopup = MutableStateFlow(false)
    val showRatePopup: StateFlow<Boolean> = _showRatePopup.asStateFlow()

    fun appDidLaunch() {
        launchCount += 1
        println("App Launch Count: $launchCount, Threshold: $nextReviewThreshold")

        if (launchCount >= nextReviewThreshold) {
            // Delay slightly to not annoy user immediately on startup
            CoroutineScope(Dispatchers.Main).launch {
                delay(1000)
                _showRatePopup.value = true
            }
        }
    }

    fun remindMeLater() {
        nextReviewThreshold = launchCount + 5
        _showRatePopup.value = false
    }

    fun rateNow() {
        // Push the next prompt far into the future so we don't ask again soon
        nextReviewThreshold = launchCount + 100
        _showRatePopup.value = false

        // Trigger the system review prompt (Intent to Play Store)
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: android.content.ActivityNotFoundException) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: RateManager? = null

        fun getInstance(context: Context): RateManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: RateManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
