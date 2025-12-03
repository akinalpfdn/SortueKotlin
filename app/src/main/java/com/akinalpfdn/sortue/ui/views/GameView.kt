package com.akinalpfdn.sortue.ui.views

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Window
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akinalpfdn.sortue.R
import com.akinalpfdn.sortue.models.GameStatus
import com.akinalpfdn.sortue.models.Tile
import com.akinalpfdn.sortue.ui.components.AboutOverlay
import com.akinalpfdn.sortue.ui.components.AmbientBackground
import com.akinalpfdn.sortue.viewmodels.GameViewModel
import kotlin.random.Random

@Composable
fun GameView(vm: GameViewModel = viewModel()) {
    val tiles by vm.tiles.collectAsState()
    val status by vm.status.collectAsState()
    val gridDimension by vm.gridDimension.collectAsState()
    val moves by vm.moves.collectAsState()
    val selectedTileId by vm.selectedTileId.collectAsState()
    val currentLevel by vm.currentLevel.collectAsState()

    var showAbout by remember { mutableStateOf(false) }

    // Controlled states for sequence
    var showConfetti by remember { mutableStateOf(false) }
    var showWinOverlay by remember { mutableStateOf(false) }

    // CORRECTED SEQUENCE LOGIC
    LaunchedEffect(status) {
        if (status == GameStatus.WON) {
            // Step 1: Celebration Starts Immediately
            // Confetti explodes at the same time the tiles start their "Wave" animation.
            showConfetti = true

            // Step 2: Wait for user to enjoy the chaos (2.5 seconds)
            // The wave takes ~0.5s - 1.0s depending on grid size.
            // The confetti fades out over ~2.5s.
            kotlinx.coroutines.delay(3500)

            // Step 3: Show Menu only after the show is mostly done
            showWinOverlay = true
        } else {
            // Reset everything if game restarts
            showConfetti = false
            showWinOverlay = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background layer
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.White))
        AmbientBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp)
                // Only blur when the overlay is actually visible
                .blur(if (showWinOverlay || showAbout) 5.dp else 0.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showAbout = true },
                    modifier = Modifier.size(44.dp)
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
                        text = stringResource(
                            R.string.level_display,
                            currentLevel,
                            gridDimension,
                            gridDimension,
                            moves
                        ).uppercase(),
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

            // Slider
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.grid_size),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black.copy(alpha = 0.6f)
                    )

                    Text(
                        text = "${gridDimension}x${gridDimension}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3F51B5)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Window,
                        contentDescription = null,
                        tint = Color.Gray.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )

                    val haptic = LocalHapticFeedback.current

                    Slider(
                        value = gridDimension.toFloat(),
                        onValueChange = { newValue ->
                            val newInt = newValue.toInt()
                            if (newInt != gridDimension) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                vm.startNewGame(dimension = newInt, preserveColors = true)
                            }
                        },
                        valueRange = 4f..12f,
                        steps = 7,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF3F51B5),
                            activeTrackColor = Color(0xFF3F51B5),
                            inactiveTrackColor = Color(0xFF3F51B5).copy(alpha = 0.15f),
                            activeTickColor = Color.Transparent,
                            inactiveTickColor = Color.Transparent
                        )
                    )

                    Icon(
                        imageVector = Icons.Filled.GridOn,
                        contentDescription = null,
                        tint = Color.Gray.copy(alpha = 0.3f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        // Overlays

        // Show Confetti immediately when WON
        if (showConfetti) {
            ConfettiSystem()
        }

        if (showWinOverlay) {
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
    val x = index % gridWidth
    val y = index / gridWidth
    val delay = (x + y) * 50

    val scale = remember { Animatable(1f) }
    val offsetY = remember { Animatable(0f) }

    LaunchedEffect(isWon) {
        if (isWon) {
            // Tiles start moving immediately (with their staggered delay)
            kotlinx.coroutines.delay(delay.toLong())
            scale.animateTo(1.1f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow))
            offsetY.animateTo(-10f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow))
        } else {
            scale.animateTo(if (isSelected) 0.9f else 1.0f)
            offsetY.animateTo(0f)
        }
    }

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

@Composable
fun PremiumWinOverlay(onReplay: () -> Unit, onNext: () -> Unit) {
    val title = "Magnificent!"
    val subtitle = "You have restored order to the chaos."

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.2f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .shadow(
                    elevation = 30.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = Color.Black.copy(alpha = 0.25f)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White.copy(alpha = 0.85f))
                .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(32.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF3F51B5).copy(alpha = 0.2f),
                                    Color(0xFF9C27B0).copy(alpha = 0.2f)
                                )
                            ),
                            shape = CircleShape
                        )
                )
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = Color(0xFF3F51B5)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif,
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onReplay,
                    modifier = Modifier
                        .size(50.dp)
                        .background(Color.Gray.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = null,
                        tint = Color.Black.copy(alpha = 0.8f)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .shadow(8.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.2f))
                        .background(Color.Black.copy(alpha = 0.7f), CircleShape)
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
fun CircleButton(icon: ImageVector, onClick: () -> Unit, enabled: Boolean = true) {
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
                imageVector = Icons.Filled.Visibility,
                contentDescription = null,
                tint = Color(0xFF3F51B5)
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = null,
                tint = Color.Black
            )
        }
    }
}

class ConfettiParticle(
    initialX: Float,
    initialY: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val size: Float
) {
    var x by mutableFloatStateOf(initialX)
    var y by mutableFloatStateOf(initialY)
    var alpha by mutableFloatStateOf(1f)
}

@Composable
fun ConfettiSystem() {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        val widthPx = with(density) { maxWidth.toPx() }
        val heightPx = with(density) { maxHeight.toPx() }

        val particles = remember {
            val random = Random(System.currentTimeMillis())
            val colors = listOf(
                Color(0xFFFF5252), Color(0xFFFF4081), Color(0xFFE040FB),
                Color(0xFF7C4DFF), Color(0xFF536DFE), Color(0xFF448AFF),
                Color(0xFF40C4FF), Color(0xFF18FFFF), Color(0xFF64FFDA),
                Color(0xFF69F0AE), Color(0xFFB2FF59), Color(0xFFFFD740),
                Color(0xFFFFAB40), Color(0xFFFF6E40)
            )

            val list = mutableStateListOf<ConfettiParticle>()
            repeat(150) {
                list.add(
                    ConfettiParticle(
                        initialX = widthPx / 2f,
                        initialY = heightPx / 2f,
                        vx = (random.nextFloat() - 0.5f) * 2000f,
                        vy = (random.nextFloat() - 0.5f) * 2000f - 500f,
                        color = colors.random(),
                        size = random.nextFloat() * 12f + 6f
                    )
                )
            }
            list
        }

        var lastFrameTime by remember { mutableStateOf(0L) }

        LaunchedEffect(Unit) {
            while (true) {
                withFrameMillis { frameTime ->
                    if (lastFrameTime != 0L) {
                        val dt = (frameTime - lastFrameTime) / 1000f
                        particles.forEach { p ->
                            p.x += p.vx * dt
                            p.y += p.vy * dt
                            p.alpha -= 0.4f * dt
                        }
                    }
                    lastFrameTime = frameTime
                }
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.forEach { p ->
                if (p.alpha > 0f) {
                    drawCircle(
                        color = p.color.copy(alpha = p.alpha.coerceIn(0f, 1f)),
                        radius = p.size,
                        center = Offset(p.x, p.y)
                    )
                }
            }
        }
    }
}