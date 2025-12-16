package com.akinalpfdn.sortue.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.akinalpfdn.sortue.models.GameStatus
import com.akinalpfdn.sortue.models.Tile
import kotlinx.coroutines.delay

@Composable
fun TileView(
    tile: Tile,
    isSelected: Boolean,
    isWon: Boolean,
    status: GameStatus,
    index: Int,
    gridWidth: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    showCheck: Boolean = true // New parameter to control check icon and visual locking
) {
    val x = index % gridWidth
    val y = index / gridWidth
    val delay = (x + y) * 50

    val scale = remember { Animatable(1f) }
    val offsetY = remember { Animatable(0f) }

    // Check if tile is in correct position (Logic for "Locking")
    // Only check if playing and not a fixed corner
    // We only consider it "visually locked" if showCheck is true
    val isCorrectlyPlaced = (status == GameStatus.PLAYING) && (tile.correctId == index) && !tile.isFixed
    val isLocked = isCorrectlyPlaced && showCheck

    LaunchedEffect(isWon) {
        if (isWon) {
            // Tiles start moving immediately (with their staggered delay)
            delay(delay.toLong())
            scale.animateTo(1.1f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow))
            offsetY.animateTo(-10f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow))
        } else {
            // Reset state if game restarts
            scale.animateTo(if (isSelected) 0.9f else (if (isLocked) 0.95f else 1.0f))
            offsetY.animateTo(0f)
        }
    }

    LaunchedEffect(isSelected, isLocked) {
        if (!isWon) {
            // Scale down slightly if locked to show it's "set"
            val targetScale = if (isSelected) 0.9f else (if (isLocked) 0.95f else 1.0f)
            scale.animateTo(targetScale)
        }
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .offset { IntOffset(0, offsetY.value.toInt()) }
            .scale(scale.value)
            .shadow(if (isSelected) 10.dp else 0.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(tile.rgb.color)
            .border(
                width = if (isSelected) 4.dp else 0.dp,
                color = Color.White,
                shape = RoundedCornerShape(8.dp)
            )
            // Add interaction source to disable ripple if needed, or rely on clickable enabled state
            .then(modifier)
            // Disable click if locked
            .then(if (onClick != null) Modifier.clickable(enabled = !tile.isFixed && !isLocked) { onClick() } else Modifier)
    ) {
        // Overlay Icons
        if (tile.isFixed) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(6.dp)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            )
        } else if (isLocked) {
            // Locked Visual (Checkmark) -- ONLY if showCheck is true
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(16.dp)
            )
        }
    }
}
