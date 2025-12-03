package com.akinalpfdn.sortue.ui.views

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lint.kotlin.metadata.Visibility
import com.akinalpfdn.sortue.R
import com.akinalpfdn.sortue.models.GameStatus
import com.akinalpfdn.sortue.models.Tile
import com.akinalpfdn.sortue.ui.components.AboutOverlay
import com.akinalpfdn.sortue.ui.components.AmbientBackground
// Removed external WinOverlay import to use the local custom implementation
import com.akinalpfdn.sortue.viewmodels.GameViewModel
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GameView(vm: GameViewModel = viewModel()) {
    val tiles by vm.tiles.collectAsState()
    val status by vm.status.collectAsState()
    val gridDimension by vm.gridDimension.collectAsState()
    val moves by vm.moves.collectAsState()
    val selectedTileId by vm.selectedTileId.collectAsState()
    val currentLevel by vm.currentLevel.collectAsState()

    var showAbout by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background layer
        Box(modifier = Modifier.fillMaxSize().background(Color.White))
        AmbientBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp)
                // Blur effect to match Swift's .blur(radius: (vm.status == .won || showAbout) ? 5 : 0)
                .blur(if (status == GameStatus.WON || showAbout) 5.dp else 0.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Icon with click animation
                IconButton(
                    onClick = { showAbout = true },
                    modifier = Modifier.size(44.dp) // Resetting size to let StatusIcon handle it
                ) {
                    StatusIcon(status = status)
                }

                Column(
                    modifier = Modifier.padding(start = 12.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.level_display, currentLevel, gridDimension, gridDimension, moves).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CircleButton(
                        icon = Icons.Filled.Lightbulb,
                        onClick = { vm.useHint() },
                        enabled = status == GameStatus.PLAYING
                    )
                    CircleButton(
                        icon = Icons.Filled.Shuffle,
                        onClick = { vm.startNewGame() },
                        enabled = status != GameStatus.PREVIEW
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Game Grid Container
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    // Matches Swift: .shadow(color: .black.opacity(0.05), radius: 20, x: 0, y: 10)
                    //.shadow(elevation = 20.dp, shape = RoundedCornerShape(24.dp), spotColor = Color.Black.copy(alpha = 0.2f))
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Transparent)
                    .padding(16.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridDimension),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    userScrollEnabled = false
                ) {
                    itemsIndexed(tiles, key = { _, it -> it.id }) { index, tile ->
                        // Wrapper box for animation modifier
                        Box(modifier = Modifier.animateItem()) {
                            TileView(
                                tile = tile,
                                isSelected = selectedTileId == tile.id,
                                isWon = status == GameStatus.WON || status == GameStatus.ANIMATING,
                                index = index,
                                gridWidth = gridDimension,
                                onClick = { vm.selectTile(tile) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Grid Size Slider
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.grid_size),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${gridDimension}x${gridDimension}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Slider(
                    value = gridDimension.toFloat(),
                    onValueChange = { newValue ->
                        val newInt = newValue.toInt()
                        if (newInt != gridDimension) {
                            vm.startNewGame(dimension = newInt, preserveColors = true)
                        }
                    },
                    valueRange = 4f..12f,
                    steps = 7,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        // Overlays
        if (status == GameStatus.WON) {
            PremiumWinOverlay(
                onReplay = { vm.startNewGame(preserveColors = true) },
                onNext = {
                    val nextDim = minOf(gridDimension + 1, 12)
                    vm.startNewGame(dimension = nextDim)
                }
            )
        }

        if (showAbout) {
            AboutOverlay(onDismiss = { showAbout = false })
        }

        if (status == GameStatus.ANIMATING) {
            ParticleSystem()
        }
    }
}

@Composable
fun TileView(
    tile: Tile,
    isSelected: Boolean,
    isWon: Boolean,
    index: Int,
    gridWidth: Int,
    onClick: () -> Unit
) {
    // Logic to calculate staggered delay based on grid position (matches Swift: delay = (x + y) * 0.05)
    val x = index % gridWidth
    val y = index / gridWidth
    val delay = (x + y) * 50 // 0.05s = 50ms

    // Scale animation with spring
    val scale = remember { Animatable(1f) }

    // Y-Offset animation for the "jump" effect
    val offsetY = remember { Animatable(0f) }

    LaunchedEffect(isWon) {
        if (isWon) {
            // Initial delay based on position
            kotlinx.coroutines.delay(delay.toLong())

            // Parallel animation: Scale up and move up
            androidx.compose.animation.core.animate(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow)
            ) { value, _ ->
                // Simulate the Swift spring animation
                // Scale 1.0 -> 1.1
                // Offset 0 -> -10
            }
            // Simplified for Compose:
            scale.animateTo(1.1f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow))
            offsetY.animateTo(-10f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow))
        } else {
            scale.animateTo(if (isSelected) 0.9f else 1.0f)
            offsetY.animateTo(0f)
        }
    }

    // React to selection changes immediately
    LaunchedEffect(isSelected) {
        if (!isWon) {
            scale.animateTo(if (isSelected) 0.9f else 1.0f)
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
            .clickable { onClick() }
    ) {
        if (tile.isFixed) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(6.dp)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            )
        }
    }
}

// Replaces the imported WinOverlay to match Swift's "Premium Glass Card"
@Composable
fun PremiumWinOverlay(onReplay: () -> Unit, onNext: () -> Unit) {
    // Placeholder for random messages (Swift uses WinMessages struct)
    val title = "Magnificent!"
    val subtitle = "You have restored order to the chaos."

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.2f)) // Dim background
            .clickable(enabled = false) {}, // Block clicks
        contentAlignment = Alignment.Center
    ) {
        // Glass Card
        Column(
            modifier = Modifier
                .padding(32.dp)
                .shadow(elevation = 30.dp, shape = RoundedCornerShape(32.dp), spotColor = Color.Black.copy(alpha = 0.25f))
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White.copy(alpha = 0.85f)) // Approximate UltraThinMaterial
                .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(32.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            // Animated Icon Gradient
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF3F51B5).copy(alpha = 0.2f), Color(0xFF9C27B0).copy(alpha = 0.2f))
                            ),
                            shape = CircleShape
                        )
                )
                Icon(
                    imageVector = Icons.Filled.Star, // Sparkles replacement
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = Color(0xFF3F51B5) // Or use a shader brush for gradient tint if advanced needed
                )
            }

            // Typography
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif, // Matches Swift "Serif" design
                        fontWeight = FontWeight.Medium
                    ),
                    color = Color.Black
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            // Actions
            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Secondary Action (Replay)
                IconButton(
                    onClick = onReplay,
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Gray.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh, // Arrow Counterclockwise replacement
                        contentDescription = null,
                        tint = Color.Black.copy(alpha = 0.8f)
                    )
                }

                // Primary Action (Next)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .shadow(8.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.2f))
                        .background(Color.Black.copy(alpha = 0.7f), CircleShape) // Primary opacity 0.5 dark
                        .clickable { onNext() },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.next_level),
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CircleButton(
    icon: ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(44.dp)
            .shadow(5.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.1f))
            .background(Color.White, CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) Color.Black else Color.Gray,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun StatusIcon(status: GameStatus) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = Color.Black.copy(alpha = 0.05f))
            .background(Color.White, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (status == GameStatus.PREVIEW) {
            Icon(
                imageVector = Icons.Filled.Visibility, // Eye icon
                contentDescription = null,
                tint = Color(0xFF3F51B5) // Indigo
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Settings, // Placeholder for Grid 2x2
                contentDescription = null,
                tint = Color.Black
            )
        }
    }
}

@Composable
fun ParticleSystem() {
    val time = remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { frameTime ->
                time.value = (frameTime / 1000f)
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val now = time.value
        val width = size.width
        val height = size.height

        for (i in 0 until 20) {
            var x = width / 2 + cos(i.toDouble() + now * 2) * 100
            var y = height / 2 + sin(i.toDouble() + now * 3) * 100

            val offset = i * 20.0
            x += cos(now + offset) * 50
            y += sin(now + offset) * 50

            drawCircle(
                color = Color.Yellow.copy(alpha = 0.8f),
                radius = 4.dp.toPx(),
                center = Offset(x.toFloat(), y.toFloat())
            )
        }
    }
}