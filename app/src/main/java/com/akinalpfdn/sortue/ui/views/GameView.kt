package com.akinalpfdn.sortue.ui.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Window
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.geometry.Rect
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akinalpfdn.sortue.R
import com.akinalpfdn.sortue.models.GameStatus
import com.akinalpfdn.sortue.models.Tile
import com.akinalpfdn.sortue.models.GameMode
import com.akinalpfdn.sortue.ui.components.AboutOverlay
import com.akinalpfdn.sortue.ui.components.AmbientBackground
import com.akinalpfdn.sortue.ui.components.GameOverOverlay
import com.akinalpfdn.sortue.ui.components.SettingsOverlay
import com.akinalpfdn.sortue.ui.components.SolutionOverlay
import com.akinalpfdn.sortue.ui.components.TileView
import com.akinalpfdn.sortue.ui.components.WinOverlay
import com.akinalpfdn.sortue.viewmodels.GameViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun GameView(vm: GameViewModel = viewModel()) {
    val tiles by vm.tiles.collectAsState()
    val status by vm.status.collectAsState()
    val gridDimension by vm.gridDimension.collectAsState()
    val moves by vm.moves.collectAsState()
    val selectedTileId by vm.selectedTileId.collectAsState()
    val currentLevel by vm.currentLevel.collectAsState()
    val minMoves by vm.minMoves.collectAsState()


    val moveLimit by vm.moveLimit.collectAsState()
    val bestTime by vm.bestTime.collectAsState()
    val bestMoves by vm.bestMoves.collectAsState()
    val gameMode by vm.gameMode.collectAsState()
    val isHapticsEnabled by vm.isHapticsEnabled.collectAsState()

    var showAbout by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var showSolutionPreview by remember { mutableStateOf(false) } // State for solution popup

    // Controlled states for sequence
    var showConfetti by remember { mutableStateOf(false) }
    var showWinOverlay by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current

    // HYBRID INTERACTION STATE
    var draggingTile by remember { mutableStateOf<Tile?>(null) }
    var pressedTileId by remember { mutableStateOf<Int?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    // Map of Tile ID to its Bounds in Root coordinates
    val itemBounds = remember { mutableMapOf<Int, Rect>() }
    // Threshold for drag detection
    val dragThreshold = with(LocalDensity.current) { 10.dp.toPx() } // Using 10.dp as safe "pixel" equivalent logic

    // CORRECTED SEQUENCE LOGIC
    LaunchedEffect(status) {
        if (status == GameStatus.WON) {
            showConfetti = true
            delay(3500)
            showWinOverlay = true
        } else {
            showConfetti = false
            showWinOverlay = false
        }
        }


    // Back Handler
    BackHandler(enabled = status == GameStatus.PLAYING || status == GameStatus.PREVIEW) {
        vm.goToMenu()
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
                .padding(vertical = 50.dp)
                // Only blur when the overlay is actually visible
                .blur(if (showWinOverlay || showAbout || showSettings || showSolutionPreview) 5.dp else 0.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Section
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 24.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box {
                        IconButton(
                            onClick = { 
                                if (status != GameStatus.PREVIEW) {
                                    showMenu = true 
                                } else {
                                    // For preview (Eye icon), maybe show About or nothing?
                                    // Current behavior was showing About. Let's keep it for consistency if clicked.
                                    showAbout = true
                                }
                            },
                            modifier = Modifier.size(44.dp)
                        ) {
                            StatusIcon(status = status)
                        }

                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.settings)) },
                                onClick = {
                                    showMenu = false
                                    showSettings = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.about)) },
                                onClick = {
                                    showMenu = false
                                    showAbout = true
                                }
                            )
                        }
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
                            text = if (gameMode == GameMode.PRECISION) {
                                val remaining = (moveLimit - moves).coerceAtLeast(0)
                                stringResource(R.string.level_display_precision, currentLevel, gridDimension, gridDimension, remaining)
                            } else {
                                stringResource(R.string.level_display, currentLevel, gridDimension, gridDimension, moves)
                            }.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        // Min Moves Display
                        Text(
                            text = stringResource(R.string.min_moves, minMoves),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray.copy(alpha = 0.8f),
                            letterSpacing = 1.sp
                        )

                        // Best Stats (Casual Mode Only)
                        if (gameMode == GameMode.CASUAL) {
                            Spacer(modifier = Modifier.height(4.dp))
                            bestTime?.let {
                                val min = it / 60
                                val sec = it % 60
                                Text(
                                    text = stringResource(R.string.best_time_fmt, min, sec),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray.copy(alpha = 0.8f),
                                    letterSpacing = 0.5.sp
                                )
                            }
                            bestMoves?.let {
                                Text(
                                    text = stringResource(R.string.best_moves_fmt, it),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray.copy(alpha = 0.8f),
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }


                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Solution Preview Button
                        if (gameMode != GameMode.PURE) {
                            CircleButton(
                                icon = Icons.Filled.Visibility, // Eye icon
                                onClick = {
                                    if (!showSolutionPreview) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        showSolutionPreview = true
                                        scope.launch {
                                            delay(2000) // Hide after 2 seconds
                                            showSolutionPreview = false
                                        }
                                    }
                                },
                                enabled = status == GameStatus.PLAYING && !showSolutionPreview
                            )
                        }

                        // Hint Button
                        if (gameMode == GameMode.CASUAL) {
                            CircleButton(
                                icon = Icons.Filled.Lightbulb,
                                onClick = { vm.useHint() },
                                enabled = status == GameStatus.PLAYING
                            )
                        }

                        // Shuffle Button (Casual Only)
                        if (gameMode == GameMode.CASUAL) {
                            CircleButton(
                                icon = Icons.Filled.Shuffle,
                                onClick = { vm.startNewGame(forceRandomShuffle = true) },
                                enabled = status != GameStatus.PREVIEW
                            )
                        }
                    }
                }
            }



            // Game Grid Container
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.Transparent)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridDimension),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    userScrollEnabled = false
                ) {
                    itemsIndexed(tiles, key = { _, it -> it.id }) { index, tile ->
                        Box(modifier = Modifier.animateItem()) {
                            val isDragging = draggingTile?.id == tile.id
                            val isLockingEnabled = gameMode != GameMode.PURE
                            TileView(
                                tile = tile,
                                isSelected = selectedTileId == tile.id || pressedTileId == tile.id || isDragging,
                                isWon = status == GameStatus.WON || status == GameStatus.ANIMATING,
                                status = status,
                                index = index,
                                gridWidth = gridDimension,
                                showCheck = isLockingEnabled, // Pass locking state
                                isHapticsEnabled = isHapticsEnabled,
                                modifier = Modifier
                                    .onGloballyPositioned { coordinates ->
                                        itemBounds[tile.id] = coordinates.boundsInRoot()
                                    }
                                    .graphicsLayer {
                                        alpha = if (isDragging) 0.6f else 1f
                                    }
                                    .pointerInput(tile.id, index, status, isLockingEnabled) {
                                        // Block interaction if Game is not playing, Tile is fixed (corner), 
                                        // OR if tile is Correct AND Locking is Enabled (Casual/Ladder)
                                        val isLocked = tile.correctId == index && isLockingEnabled
                                        if (status != GameStatus.PLAYING || tile.isFixed || isLocked) return@pointerInput
                                        
                                        awaitEachGesture {
                                            val down = awaitFirstDown(requireUnconsumed = false)
                                            val startPoint = down.position
                                            var dragStarted = false
                                            
                                            // 1. Touch Down: Highlight
                                            pressedTileId = tile.id
                                            
                                            // Loop to detect Move or Up
                                            do {
                                                val event = awaitPointerEvent()
                                                val change = event.changes.firstOrNull() ?: break
                                                
                                                val currentPoint = change.position
                                                val dist = (currentPoint - startPoint).getDistance()
                                                
                                                if (!dragStarted && dist > dragThreshold) {
                                                    dragStarted = true
                                                    draggingTile = tile
                                                    pressedTileId = null 
                                                    
                                                    
                                                    // We want the tile to center on the finger ideally, or stick to offset
                                                    // Simple approach: Center on finger
                                                    val bounds = itemBounds[tile.id] ?: Rect.Zero
                                                    dragPosition = bounds.topLeft + (currentPoint) - Offset(bounds.width/2, bounds.height/2)
                                                    // Better: Keep relative offset. 
                                                    // But for "Swap", centering feels good. Let's calculate precise relative calc if needed.
                                                    // Let's stick to: "dragPosition" is the visual TopLeft of the floating tile.
                                                    // Initial dragPos = Bounds.TopLeft
                                                    dragPosition = (itemBounds[tile.id]?.topLeft ?: Offset.Zero) + (currentPoint - startPoint)
                                                }
                                                
                                                if (dragStarted) {
                                                    dragPosition += change.positionChange()
                                                    change.consume()
                                                }
                                                
                                            } while (event.changes.any { it.pressed })
                                            
                                            // 3. Touch Up
                                            pressedTileId = null
                                            
                                            if (dragStarted) {
                                                // Drop Logic
                                                val dropCenter = dragPosition + Offset(
                                                    (itemBounds[tile.id]?.width ?: 0f) / 2,
                                                    (itemBounds[tile.id]?.height ?: 0f) / 2
                                                )
                                                // Find target
                                                val targetId = itemBounds.entries.firstOrNull { (_, rect) ->
                                                    rect.contains(dropCenter)
                                                }?.key
                                                
                                                if (targetId != null && targetId != tile.id) {
                                                    // Check if target is fixed or locked
                                                    val targetTile = tiles.find { it.id == targetId }
                                                    if (targetTile != null && !targetTile.isFixed) {
                                                        // Prevent swapping if target is already correct AND locking is enabled
                                                        val isTargetLocked = (targetTile.correctId == targetTile.currentIdx) && isLockingEnabled
                                                        
                                                        if (!isTargetLocked) {
                                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                            vm.swapTiles(tile.id, targetId)
                                                        }
                                                    }
                                                }
                                                draggingTile = null
                                                dragStarted = false
                                            } else {
                                                // Tap Logic
                                                // If we didn't drag, it's a tap.
                                                // Prevent interaction if already correct (reusing existing logic)
                                                // Prevent interaction if already correct AND locking enabled
                                                if (status == GameStatus.PLAYING && tile.correctId == index && !tile.isFixed && isLockingEnabled) {
                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                } else {
                                                    vm.selectTile(tile)
                                                }
                                            }
                                        }
                                    }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            // Slider (Casual Mode Only)
            if (gameMode == GameMode.CASUAL) {
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


        }
        }

        // Overlays



        if (status == GameStatus.MENU) {
            ModeSelectionView(
                onStartGame = { mode, size ->
                    vm.playOrResumeGame(mode, size)
                },
                onSettingsClick = {
                    showSettings = true
                },
                onAboutClick = {
                    showAbout = true
                }
            )
        }

        // Overlays (Moved to bottom for correct Z-index)
        if (showConfetti) {
            ConfettiSystem()
        }

        if (showWinOverlay) {
            WinOverlay(
                onReplay = { vm.startNewGame(preserveColors = true) },
                onNext = {
                    val nextDim = gridDimension // Keep logic same as swift version if needed
                    vm.startNewGame(dimension = nextDim)
                },
                showReplay = gameMode != GameMode.PRECISION
            )
        }

        if (showAbout) {
            AboutOverlay(onDismiss = { showAbout = false })
        }

        if (showSettings) {
             SettingsOverlay(
                 onDismiss = { showSettings = false },
                 isHapticsEnabled = isHapticsEnabled,
                 onHapticsChange = { vm.toggleHaptics(it) }
             )
        }

        if (status == GameStatus.GAME_OVER) {
            GameOverOverlay(
                onRetry = { vm.startNewGame(dimension = gridDimension, mode = gameMode) },
                onMenu = { vm.goToMenu() }
            )
        }


        // Solution Preview Overlay
        AnimatedVisibility(
            visible = showSolutionPreview,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            SolutionOverlay(tiles = tiles, gridDimension = gridDimension)
        }

        // Dragging Overlay
        draggingTile?.let { tile ->
            val tileSize = itemBounds[tile.id]?.size ?: androidx.compose.ui.geometry.Size(100f, 100f)
            // Convert pixels to dp for Size
            val density = LocalDensity.current
            val widthDp = with(density) { tileSize.width.toDp() }
            val heightDp = with(density) { tileSize.height.toDp() }
            
            // We need to match the TileView signature
            // We use a Box at the Root to position it absolutely
            Box(
                modifier = Modifier
                    .offset { IntOffset(dragPosition.x.toInt(), dragPosition.y.toInt()) }
                    .size(widthDp, heightDp)
                    .zIndex(100f) // Ensure on top
            ) {
                 TileView(
                    tile = tile,
                    isSelected = true, // Highlighted while dragging
                    isWon = false,
                    status = GameStatus.PLAYING,
                    index = 0, // irrelevant for visual only
                    gridWidth = gridDimension,
                    modifier = Modifier.fillMaxSize(), // Fill the box
                    showCheck = gameMode != GameMode.PURE
                )
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



// SettingsOverlay moved to com.akinalpfdn.sortue.ui.components.SettingsOverlay


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