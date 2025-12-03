package com.akinalpfdn.sortue

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.zIndex
import com.akinalpfdn.sortue.ui.components.RateOverlay
import com.akinalpfdn.sortue.ui.views.GameView
import com.akinalpfdn.sortue.ui.views.LandingView
import com.akinalpfdn.sortue.utils.AudioManager
import com.akinalpfdn.sortue.utils.RateManager

@Composable
fun SortueApp() {
    val context = LocalContext.current
    val rateManager = remember { RateManager.getInstance(context) }
    val showRatePopup by rateManager.showRatePopup.collectAsState()
    
    var showLanding by remember { mutableStateOf(!rateManager.hasSeenLanding) }

    LaunchedEffect(Unit) {
        AudioManager.getInstance(context).playBackgroundMusic()
        rateManager.appDidLaunch()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = showLanding,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.zIndex(1f)
        ) {
            LandingView(onPlay = {
                showLanding = false
                rateManager.hasSeenLanding = true
            })
        }

        AnimatedVisibility(
            visible = !showLanding,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.zIndex(0f)
        ) {
            GameView()
        }

        if (showRatePopup && !showLanding) {
            Box(modifier = Modifier.zIndex(2f)) {
                RateOverlay(
                    onRate = { rateManager.rateNow() },
                    onRemind = { rateManager.remindMeLater() }
                )
            }
        }
    }
}
