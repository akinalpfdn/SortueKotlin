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
    val id: Int,
    val correctId: Int,
    val color: Color,
    val isFixed: Boolean
)

@Composable
fun LandingView(
    onDismiss: () -> Unit
) {
    // Page count is 5: 4 content pages + 1 dummy page for "Swipe to Finish" detection
    val pagerState = rememberPagerState(pageCount = { 5 })
    val scope = rememberCoroutineScope()

    // Swipe-to-Finish Logic: If user reaches the 5th page (index 4), close the tutorial
    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage == 4) {
            onDismiss()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        LandingAmbientBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp, bottom = 24.dp), // Reduced bottom padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Header (Skip Button)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(40.dp), // Fixed height for header
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hide Skip button on the last real page (index 3)
                if (pagerState.currentPage < 3) {
                    TextButton(onClick = onDismiss) {
                        Text("Skip", color = Color.Gray)
                    }
                }
            }

            // 2. Main Content Pager (Takes ALL available space)
            // This fixes the layout issue on small screens by pushing content into the empty space
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Crucial: Fills all vertical space between header and footer
            ) { page ->
                if (page < 4) {
                    OnboardingPageContent(page = page)
                } else {
                    // Dummy page content (invisible)
                    Box(modifier = Modifier.fillMaxSize())
                }
            }

            // 3. Bottom Controls (Indicators + Button)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp) // Add little breathing room from pager
            ) {
                // Page Indicators (Only show 4 dots)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(4) { index ->
                        // If we are on dummy page 4, keep the last dot selected
                        val displayPage = if (pagerState.currentPage > 3) 3 else pagerState.currentPage
                        val isSelected = displayPage == index

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
                            text = if (pagerState.currentPage >= 3) "Start Sorting" else "Continue",
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
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // Centers content vertically in the available space
    ) {

        Spacer(modifier = Modifier.weight(1f)) // Push content towards center

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

        Spacer(modifier = Modifier.weight(1f)) // Push content towards center
    }
}

// ... [AutoSolvingGridAnimation, LandingTileView, WelcomeAnimation, SwapMechanicAnimation, DifficultyAnimation, LandingAmbientBackground remain unchanged] ...
// (Include them here exactly as they were in the previous version to ensure the file is complete)

// MARK: - Auto Solving Grid Animation (Page 2)

@Composable
fun AutoSolvingGridAnimation() {
    val gridSize = 4
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

    var currentGrid by remember { mutableStateOf(solvedTiles) }
    var isWon by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
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

            var solving = true
            while (solving) {
                val currentList = currentGrid.toMutableList()
                val wrongIndex = currentList.indexOfFirst {
                    it.correctId != currentList.indexOf(it) && !it.isFixed
                }

                if (wrongIndex != -1) {
                    val tile = currentList[wrongIndex]
                    val correctPos = tile.correctId
                    val temp = currentList[wrongIndex]
                    currentList[wrongIndex] = currentList[correctPos]
                    currentList[correctPos] = temp
                    currentGrid = currentList.toList() as MutableList<LandingTile>
                    delay(500)
                } else {
                    solving = false
                }
            }
            isWon = true
            delay(3000)
        }
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
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
                Box(modifier = Modifier.animateItem()) {
                    val index = currentGrid.indexOf(tile)
                    LandingTileView(tile = tile, isWon = isWon, index = index, gridWidth = gridSize)
                }
            }
        }
    }
}

@Composable
private fun LandingTileView(tile: LandingTile, isWon: Boolean, index: Int, gridWidth: Int) {
    val x = index % gridWidth
    val y = index / gridWidth
    val staggerDelay = (x + y) * 50
    val scale = remember { Animatable(1f) }
    val offsetY = remember { Animatable(0f) }
    val isCorrectlyPlaced = (tile.correctId == index) && !tile.isFixed

    LaunchedEffect(isWon) {
        if (isWon) {
            delay(staggerDelay.toLong())
            scale.animateTo(1.1f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow))
            offsetY.animateTo(-10f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow))
        } else {
            scale.animateTo(if (isCorrectlyPlaced) 0.95f else 1.0f)
            offsetY.animateTo(0f)
        }
    }
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
            .shadow(0.dp, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
            .background(tile.color)
            .border(width = 0.dp, color = Color.White, shape = RoundedCornerShape(8.dp))
    ) {
        if (tile.isFixed) {
            Box(
                modifier = Modifier.align(Alignment.Center).size(6.dp)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            )
        } else if (isCorrectlyPlaced) {
            Icon(
                imageVector = Icons.Filled.Check, contentDescription = null,
                tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.align(Alignment.Center).size(16.dp)
            )
        }
    }
}

@Composable
fun WelcomeAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f, targetValue = 1.05f,
        animationSpec = infiniteRepeatable(animation = tween(2000, easing = LinearEasing), repeatMode = RepeatMode.Reverse),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(200.dp)
            .scale(scale)
            .shadow(20.dp, CircleShape, spotColor = Color(0xFF9C27B0).copy(alpha = 0.3f))
            .background(
                brush = Brush.sweepGradient(
                    colors = listOf(Color(0xFF3F51B5), Color(0xFFE040FB), Color(0xFFFF4081), Color(0xFF3F51B5))
                ), shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.size(190.dp).background(Color.White, CircleShape))
        Text(
            text = "Sortue",
            style = MaterialTheme.typography.displayLarge.copy(
                brush = Brush.linearGradient(colors = listOf(Color(0xFF3F51B5), Color(0xFFE040FB)))
            ),
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
fun SwapMechanicAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "swap")
    val fraction by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(2000, easing = FastOutSlowInEasing, delayMillis = 500), repeatMode = RepeatMode.Restart),
        label = "fraction"
    )
    val moveProgress = (fraction * 2.5f).coerceIn(0f, 1f)
    val resetPhase = fraction > 0.8f
    val offsetA = 100.dp * moveProgress
    val offsetB = -100.dp * moveProgress

    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier.offset(x = -50.dp + offsetA).size(80.dp)
                .graphicsLayer { alpha = if (resetPhase) 0f else 1f }
                .shadow(8.dp, RoundedCornerShape(12.dp))
                .background(Color(0xFF3F51B5), RoundedCornerShape(12.dp))
        )
        Box(
            modifier = Modifier.offset(x = 50.dp + offsetB).size(80.dp)
                .graphicsLayer { alpha = if (resetPhase) 0f else 1f }
                .shadow(8.dp, RoundedCornerShape(12.dp))
                .background(Color(0xFFE040FB), RoundedCornerShape(12.dp))
        )
        Box(
            modifier = Modifier.offset(x = -50.dp + offsetA + 20.dp, y = 30.dp)
                .graphicsLayer {
                    alpha = if (resetPhase) 0f else 1f
                    scaleX = if (moveProgress > 0.1 && moveProgress < 0.9) 0.9f else 1f
                    scaleY = if (moveProgress > 0.1 && moveProgress < 0.9) 0.9f else 1f
                }
        ) {
            Icon(
                imageVector = Icons.Filled.TouchApp, contentDescription = null,
                tint = Color.Black.copy(alpha = 0.5f), modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun DifficultyAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "size")
    val toggle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(2000, easing = EaseInOutCubic), repeatMode = RepeatMode.Reverse),
        label = "toggle"
    )
    val spacing = 4.dp
    val boxSize = 200.dp

    Box(
        modifier = Modifier.size(boxSize).background(Color.White, RoundedCornerShape(16.dp))
            .border(2.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp)),
        contentAlignment = Alignment.Center
    ) {
        val count = if (toggle < 0.5f) 3 else 5
        val tileSize = (160.dp / count) - spacing
        Column(verticalArrangement = Arrangement.spacedBy(spacing), horizontalAlignment = Alignment.CenterHorizontally) {
            repeat(count) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                    repeat(count) { col ->
                        Box(
                            modifier = Modifier.size(tileSize).clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF3F51B5).copy(alpha = 0.3f + ((row + col).toFloat() / (count * 2)) * 0.7f))
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
            brush = Brush.radialGradient(colors = listOf(Color(0xFFE3F2FD), Color.Transparent), center = Offset(size.width * 0.8f, size.height * 0.1f), radius = size.width * 0.6f)
        )
        drawCircle(
            brush = Brush.radialGradient(colors = listOf(Color(0xFFF3E5F5), Color.Transparent), center = Offset(size.width * 0.1f, size.height * 0.9f), radius = size.width * 0.5f)
        )
    }
}