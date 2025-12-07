package com.akinalpfdn.sortue.ui.views

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Simple data model for the landing animation
data class LandingTile(
    val id: Int,        // Unique ID for LazyGrid keys
    val correctId: Int, // Where it belongs
    val color: Color,
    val isFixed: Boolean
)

@Composable
fun LandingView(
    onDismiss: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        LandingAmbientBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                if (pagerState.currentPage < 3) {
                    TextButton(onClick = onDismiss) {
                        Text("Skip", color = Color.Gray)
                    }
                } else {
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Main Content Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
            ) { page ->
                OnboardingPageContent(page = page)
            }

            Spacer(modifier = Modifier.weight(0.5f))

            // Bottom Controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                // Page Indicators
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(4) { index ->
                        val isSelected = pagerState.currentPage == index
                        val width by animateDpAsState(
                            targetValue = if (isSelected) 24.dp else 8.dp,
                            label = "dotWidth"
                        )
                        val color by animateColorAsState(
                            targetValue = if (isSelected) Color(0xFF3F51B5) else Color.LightGray,
                            label = "dotColor"
                        )

                        Box(
                            modifier = Modifier
                                .height(8.dp)
                                .width(width)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }

                // Primary Action Button
                Button(
                    onClick = {
                        scope.launch {
                            if (pagerState.currentPage < 3) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                onDismiss()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp), spotColor = Color(0xFF3F51B5).copy(alpha = 0.25f)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3F51B5),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (pagerState.currentPage == 3) "Start Sorting" else "Continue",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (pagerState.currentPage < 3) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animation Container
        Box(
            modifier = Modifier
                .size(280.dp)
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            when (page) {
                0 -> WelcomeAnimation()
                1 -> SwapMechanicAnimation()
                2 -> AutoSolvingGridAnimation()
                3 -> DifficultyAnimation()
            }
        }

        val title = when (page) {
            0 -> "Welcome to Sortue"
            1 -> "Swap & Solve"
            2 -> "Find the Harmony"
            3 -> "Challenge Yourself"
            else -> ""
        }

        val description = when (page) {
            0 -> "Relax your mind with beautiful color gradient puzzles."
            1 -> "Drag any tile to swap it with another. Put the colors in the right place."
            2 -> "Watch the colors flow. Solve the puzzle to reveal the perfect gradient."
            3 -> "Adjust the grid size to match your mood, from casual 4x4 to expert 12x12."
            else -> ""
        }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

// MARK: - Auto Solving Grid Animation (Page 2)

@Composable
fun AutoSolvingGridAnimation() {
    val gridSize = 4
    // 1. Generate solved state colors
    val solvedTiles = remember {
        val tiles = mutableListOf<LandingTile>()
        val c1 = Color(0xFF3F51B5) // TL
        val c2 = Color(0xFFE040FB) // TR
        val c3 = Color(0xFF00BCD4) // BL
        val c4 = Color(0xFF18FFFF) // BR

        for (y in 0 until gridSize) {
            for (x in 0 until gridSize) {
                val xFrac = x.toFloat() / (gridSize - 1)
                val yFrac = y.toFloat() / (gridSize - 1)
                val top = lerp(c1, c2, xFrac)
                val bottom = lerp(c3, c4, xFrac)
                val finalColor = lerp(top, bottom, yFrac)

                val isFixed = (x == 0 && y == 0) || (x == gridSize - 1 && y == 0) ||
                        (x == 0 && y == gridSize - 1) || (x == gridSize - 1 && y == gridSize - 1)

                val id = y * gridSize + x
                tiles.add(LandingTile(id = id, correctId = id, color = finalColor, isFixed = isFixed))
            }
        }
        tiles
    }

    // Mutable state for the grid
    var currentGrid by remember { mutableStateOf(solvedTiles) }
    var isWon by remember { mutableStateOf(false) }

    // Logic Loop
    LaunchedEffect(Unit) {
        while (true) {
            // A. Shuffle (Keep corners fixed)
            isWon = false
            val movable = solvedTiles.filter { !it.isFixed }.toMutableList()
            movable.shuffle()

            val newGrid = arrayOfNulls<LandingTile>(gridSize * gridSize)
            solvedTiles.filter { it.isFixed }.forEach { newGrid[it.correctId] = it }

            var mIdx = 0
            for (i in newGrid.indices) {
                if (newGrid[i] == null) {
                    newGrid[i] = movable[mIdx++]
                }
            }
            currentGrid = newGrid.filterNotNull() as MutableList<LandingTile>

            delay(1000)

            // B. Solve Loop (Simulate Hint clicks)
            var solving = true
            while (solving) {
                val currentList = currentGrid.toMutableList()

                // Find first wrong tile
                val wrongIndex = currentList.indexOfFirst {
                    it.correctId != currentList.indexOf(it) && !it.isFixed
                }

                if (wrongIndex != -1) {
                    val tile = currentList[wrongIndex]
                    val targetIndex = currentList.indexOfFirst { it.correctId == tile.correctId } // Actually, target is where it belongs.
                    // Note: In GameViewModel useHint logic: we look for a tile that is wrong,
                    // and we swap it to where it belongs (its correctId).

                    val correctPos = tile.correctId

                    // Swap logic
                    val temp = currentList[wrongIndex]
                    currentList[wrongIndex] = currentList[correctPos]
                    currentList[correctPos] = temp

                    currentGrid = currentList.toList() as MutableList<LandingTile>
                    delay(500) // Speed of hint animations
                } else {
                    solving = false
                }
            }

            // C. Win State
            isWon = true
            delay(3000) // Show win animation
        }
    }

    // UI Rendering - Using LazyVerticalGrid to match GameView
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp)) // Matched GameView corner radius
            .background(Color.Transparent)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(gridSize),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            userScrollEnabled = false,
            modifier = Modifier.fillMaxSize()
        ) {
            items(currentGrid, key = { it.id }) { tile ->
                // Use animateItem modifier for the smooth swap effect
                Box(modifier = Modifier.animateItem()) {
                    val index = currentGrid.indexOf(tile)

                    LandingTileView(
                        tile = tile,
                        isWon = isWon,
                        index = index, // Current index in grid
                        gridWidth = gridSize
                    )
                }
            }
        }
    }
}

// Replicating TileView from GameView.kt EXACTLY
@Composable
private fun LandingTileView(
    tile: LandingTile,
    isWon: Boolean,
    index: Int,
    gridWidth: Int
) {
    val x = index % gridWidth
    val y = index / gridWidth
    val staggerDelay = (x + y) * 50

    val scale = remember { Animatable(1f) }
    val offsetY = remember { Animatable(0f) }

    // Logic for "Locking" visual (Checkmark)
    // In game: (status == PLAYING) && (correctId == index) && !isFixed
    // Here: we treat "playing" as !isWon.
    val isCorrectlyPlaced = (tile.correctId == index) && !tile.isFixed

    LaunchedEffect(isWon) {
        if (isWon) {
            // Tiles start moving immediately (with their staggered delay)
            delay(staggerDelay.toLong())
            scale.animateTo(1.1f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow))
            offsetY.animateTo(-10f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow))
        } else {
            // Reset state
            scale.animateTo(if (isCorrectlyPlaced) 0.95f else 1.0f)
            offsetY.animateTo(0f)
        }
    }

    // React to correct placement during the solve loop
    LaunchedEffect(isCorrectlyPlaced, isWon) {
        if (!isWon) {
            val targetScale = if (isCorrectlyPlaced) 0.95f else 1.0f
            scale.animateTo(targetScale)
        }
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .offset { IntOffset(0, offsetY.value.toInt()) }
            .scale(scale.value)
            // No selection state in landing demo, so shadow is constant 0 or small?
            // GameView uses 0.dp if not selected.
            .shadow(0.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(tile.color)
            // Border logic from GameView (removed selection part as we don't select here)
            .border(
                width = 0.dp,
                color = Color.White,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        // Overlay Icons
        if (tile.isFixed) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(6.dp)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            )
        } else if (isCorrectlyPlaced) {
            // Locked Visual (Checkmark)
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

// MARK: - Other Animations (Preserved)

@Composable
fun WelcomeAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(200.dp)
            .scale(scale)
            .shadow(20.dp, CircleShape, spotColor = Color(0xFF9C27B0).copy(alpha = 0.3f))
            .background(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFF3F51B5),
                        Color(0xFFE040FB),
                        Color(0xFFFF4081),
                        Color(0xFF3F51B5)
                    )
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(190.dp)
                .background(Color.White, CircleShape)
        )
        Text(
            text = "Sortue",
            style = MaterialTheme.typography.displayLarge.copy(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF3F51B5), Color(0xFFE040FB))
                )
            ),
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun SwapMechanicAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "swap")
    val fraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing, delayMillis = 500),
            repeatMode = RepeatMode.Restart
        ),
        label = "fraction"
    )

    val moveProgress = (fraction * 2.5f).coerceIn(0f, 1f)
    val resetPhase = fraction > 0.8f

    val offsetA = 100.dp * moveProgress
    val offsetB = -100.dp * moveProgress

    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .offset(x = -50.dp + offsetA)
                .size(80.dp)
                .graphicsLayer { alpha = if (resetPhase) 0f else 1f }
                .shadow(8.dp, RoundedCornerShape(12.dp))
                .background(Color(0xFF3F51B5), RoundedCornerShape(12.dp))
        )

        Box(
            modifier = Modifier
                .offset(x = 50.dp + offsetB)
                .size(80.dp)
                .graphicsLayer { alpha = if (resetPhase) 0f else 1f }
                .shadow(8.dp, RoundedCornerShape(12.dp))
                .background(Color(0xFFE040FB), RoundedCornerShape(12.dp))
        )

        Box(
            modifier = Modifier
                .offset(x = -50.dp + offsetA + 20.dp, y = 30.dp)
                .graphicsLayer {
                    alpha = if (resetPhase) 0f else 1f
                    scaleX = if (moveProgress > 0.1 && moveProgress < 0.9) 0.9f else 1f
                    scaleY = if (moveProgress > 0.1 && moveProgress < 0.9) 0.9f else 1f
                }
        ) {
            Icon(
                imageVector = Icons.Filled.TouchApp,
                contentDescription = null,
                tint = Color.Black.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun DifficultyAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "size")
    val toggle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "toggle"
    )

    val spacing = 4.dp
    val boxSize = 200.dp

    Box(
        modifier = Modifier
            .size(boxSize)
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(2.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        val count = if (toggle < 0.5f) 3 else 5
        val tileSize = (160.dp / count) - spacing

        Column(
            verticalArrangement = Arrangement.spacedBy(spacing),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(count) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                    repeat(count) { col ->
                        Box(
                            modifier = Modifier
                                .size(tileSize)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Color(0xFF3F51B5).copy(
                                        alpha = 0.3f + ((row + col).toFloat() / (count * 2)) * 0.7f
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LandingAmbientBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFE3F2FD), Color.Transparent),
                center = Offset(size.width * 0.8f, size.height * 0.1f),
                radius = size.width * 0.6f
            )
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFF3E5F5), Color.Transparent),
                center = Offset(size.width * 0.1f, size.height * 0.9f),
                radius = size.width * 0.5f
            )
        )
    }
}