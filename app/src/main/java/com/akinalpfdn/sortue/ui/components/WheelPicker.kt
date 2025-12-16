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
import androidx.compose.ui.platform.LocalHapticFeedback
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
    val haptic = LocalHapticFeedback.current

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
            val viewportCenter = layoutInfo.viewportEndOffset / 2
            
            // Find the item visually closest to the center
            val closestItem = layoutInfo.visibleItemsInfo
                .minByOrNull { abs((it.offset + it.size / 2) - viewportCenter) }
            
            closestItem?.index ?: 0
        }
    }

    // Report selection updates
    LaunchedEffect(centeredIndex) {
        val actualIndex = centeredIndex % items.size
        onSelectionChanged(actualIndex)
        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
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
        
        // Modern "Samsung-style" Dividers
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = -(itemHeight / 2)),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            thickness = 1.dp
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = itemHeight / 2),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            thickness = 1.dp
        )

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
                val isSelected = index == centeredIndex

                // 3D Drum Effect Calculations
                // This creates the "Samsung Timer" cylindrical look
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .graphicsLayer {
                            // Calculate distance from center in pixels
                            val layoutInfo = listState.layoutInfo
                            val viewportCenter = layoutInfo.viewportEndOffset / 2f
                            // We use the item's current visual offset
                            // Note: standard item block does not give absolute offset easily, 
                            // but we can infer it if we assume stable layout.
                            // However, using the "isSelected" state for simple boolean styling is safer/cheaper.
                            
                            // For true smooth rotation, we need the derived offset:
                            // We accept a slight recomposition hit for the visual effect here.
                            val distanceInPx = (index - centeredIndex) * itemHeightPx 
                            // (Approximation for rotation calculation)
                            
                            // Visual parameters
                            val maxDistance = (visibleItemsCount * itemHeightPx) / 2f
                            val distance = abs(distanceInPx).coerceAtMost(maxDistance)
                            
                            // Rotation X: Items at top rotate down, items at bottom rotate up
                            val rotationAngle = if (index < centeredIndex) 45f else -45f
                            // Smooth interpolation based on distance
                            val rotationFactor = (distance / maxDistance).coerceIn(0f, 1f)
                            
                            rotationX = rotationAngle * rotationFactor
                            scaleX = 1f - (rotationFactor * 0.15f)
                            scaleY = 1f - (rotationFactor * 0.15f)
                            alpha = 1f - (rotationFactor * 0.7f)
                            
                            // Standardize selection exact center
                            if (isSelected) {
                                alpha = 1f
                                rotationX = 0f
                                scaleX = 1.1f
                                scaleY = 1.1f
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    itemContent(actualItem, isSelected)
                }
            }
        }
    }
}