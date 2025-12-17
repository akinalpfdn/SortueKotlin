package com.akinalpfdn.sortue.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalDensity
// import androidx.compose.ui.platform.LocalHapticFeedback // Removed
import androidx.compose.ui.platform.LocalView
import android.view.HapticFeedbackConstants
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlin.math.abs

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> WheelPicker(
    modifier: Modifier = Modifier,
    items: List<T>,
    initialIndex: Int = 0,
    visibleItemsCount: Int = 5,
    itemHeight: Dp = 60.dp,
    onSelectionChanged: (Int) -> Unit,
    itemContent: @Composable (item: T, isSelected: Boolean) -> Unit
) {
    val density = LocalDensity.current
    val itemHeightPx = with(density) { itemHeight.toPx() }
    val context = LocalContext.current

    // Buffer Strategy:
    // We don't use Int.MAX_VALUE. We use a "reasonable" buffer (e.g., 300) to allow for
    // strong flings. When the list stops scrolling, we silently reset the position to the center.
    val bufferMultiplier = 300
    val loopCenter = bufferMultiplier / 2
    
    // Calculate the start index in the middle of our buffer
    val initialListIndex = loopCenter * items.size + initialIndex
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialListIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    // Calculate the central item index efficiently
    val centeredIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val viewportCenter = (layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset) / 2
            
            // Find the item visually closest to the center
            val closestItem = layoutInfo.visibleItemsInfo
                .minByOrNull { abs((it.offset + it.size / 2) - viewportCenter) }
            
            closestItem?.index ?: initialListIndex
        }
    }

    // Report selection updates
    // Report selection updates
    LaunchedEffect(centeredIndex) {
        val actualIndex = centeredIndex % items.size
        onSelectionChanged(actualIndex)
        
        // Direct Vibration Logic
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(20) // 20ms fallback for old devices
            }
        }
    }

    // THE RESET LOGIC:
    // When scrolling stops, if we are too far from the center loop, jump back to the center.
    // This maintains the illusion of infinity without massive memory index issues.
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .filter { !it } // Only run when scroll stops
            .collect {
                val currentRealIndex = centeredIndex % items.size
                // Calculate the "ideal" index in the absolute center of our buffer
                val idealIndex = (loopCenter * items.size) + currentRealIndex
                
                // If we have drifted significantly, reset state silently
                // We check if we are outside a safe range (e.g., +/- 50 sets of items)
                if (abs(centeredIndex - idealIndex) > items.size * 50) {
                     listState.scrollToItem(idealIndex, listState.firstVisibleItemScrollOffset)
                }
            }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight * visibleItemsCount),
        contentAlignment = Alignment.Center
    ) {
        
        // Dividers removed for cleaner "floating" look as requested


        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            contentPadding = PaddingValues(vertical = itemHeight * (visibleItemsCount / 2)),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                count = items.size * bufferMultiplier,
                key = { index -> index } // Unique keys for the buffer
            ) { index ->
                val actualItem = items[index % items.size]
                // We calculate "isSelected" based on the centeredIndex state we derived, 
                // but for smooth visual transitions (scaling/alpha) during scroll, 
                // we should rely on the distance calculation in graphicsLayer if possible, 
                // OR use the isSelected boolean if we accept 'snap' visuals.
                // User wants "Compact", "Current Bigger", "Opacity for others".
                
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .graphicsLayer {
                            val itemCenter = (index * itemHeightPx) + (itemHeightPx / 2)
                            val layoutInfo = listState.layoutInfo
                            val viewportCenter = layoutInfo.viewportEndOffset / 2f + layoutInfo.viewportStartOffset
                            
                            // Calculate current absolute position of item center relative to viewport
                            // This is tricky with LazyList as layoutInfo gives visible items.
                            // Let's deduce distance from center based on index vs centeredIndex (approx stable)
                            // or better: map the item's offset if visible.
                            
                            val visibleItemInfo = layoutInfo.visibleItemsInfo.find { it.index == index }
                            val distanceFromCenter = if (visibleItemInfo != null) {
                                val itemMid = visibleItemInfo.offset + (visibleItemInfo.size / 2f)
                                abs(viewportCenter - itemMid)
                            } else {
                                // If not visible, effectively huge visual distance
                                (visibleItemsCount * itemHeightPx)
                            }

                            val maxDistance = (visibleItemsCount * itemHeightPx) / 2f
                            val distanceFraction = (distanceFromCenter / maxDistance).coerceIn(0f, 1f)
                            
                            // Visual Transform Logic
                            // Center (distance 0) -> Scale 1.1f, Alpha 1f
                            // Edge (distance 1) -> Scale 0.8f, Alpha 0.3f
                            
                            val scale = androidx.compose.ui.util.lerp(1.2f, 0.7f, distanceFraction)
                            val alphaValue = androidx.compose.ui.util.lerp(1f, 0.3f, distanceFraction)
                            
                            scaleX = scale
                            scaleY = scale
                            alpha = alphaValue
                            
                            // 3D Rotation (optional, kept subtle)
                            val rotationLimit = 25f
                            val rotation = rotationLimit * (distanceFromCenter / (maxDistance)) 
                            // Determine direction
                            // We can't easily retrieve "direction" from just distance, need sign.
                            // But usually rotationX is positive for top items, negative for bottom.
                            // visibleItemInfo.offset < viewportCenter -> Top items
                            if (visibleItemInfo != null) {
                                val itemMid = visibleItemInfo.offset + (visibleItemInfo.size / 2f)
                                rotationX = if (itemMid < viewportCenter) rotation else -rotation
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    itemContent(actualItem, index == centeredIndex)
                }
            }
        }
    }
}