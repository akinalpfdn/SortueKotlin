package com.akinalpfdn.sortue.ui.views

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.akinalpfdn.sortue.R
import com.akinalpfdn.sortue.models.GameStatus
import com.akinalpfdn.sortue.models.Tile
import com.akinalpfdn.sortue.ui.components.AboutOverlay
import com.akinalpfdn.sortue.ui.components.AmbientBackground
import com.akinalpfdn.sortue.ui.components.WinOverlay
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
        AmbientBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 20.dp)
                .blur(if (status == GameStatus.WON || showAbout) 5.dp else 0.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showAbout = true }) {
                    StatusIcon(status = status)
                }

                Column(
                    modifier = Modifier.padding(start = 8.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.level_display, currentLevel, gridDimension, gridDimension, moves),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CircleButton(
                        icon = Icons.Filled.Info, // Lightbulb replacement
                        onClick = { vm.useHint() },
                        enabled = status == GameStatus.PLAYING
                    )
                    CircleButton(
                        icon = Icons.Filled.Refresh, // Shuffle replacement
                        onClick = { vm.startNewGame() },
                        enabled = status != GameStatus.PREVIEW
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Game Grid
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .shadow(20.dp, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(gridDimension),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    userScrollEnabled = false
                ) {
                    items(tiles, key = { it.id }) { tile ->
                        TileView(
                            tile = tile,
                            isSelected = selectedTileId == tile.id,
                            isWon = status == GameStatus.WON || status == GameStatus.ANIMATING,
                            onClick = { vm.selectTile(tile) }
                        )
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
                    steps = 7, // (12-4) - 1
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        if (status == GameStatus.WON) {
            WinOverlay(
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
    onClick: () -> Unit
) {
    // Simple scale animation
    val scale by animateFloatAsState(
        targetValue = if (isWon) 1.1f else if (isSelected) 0.9f else 1.0f,
        animationSpec = tween(durationMillis = 300),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .scale(scale)
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
fun CircleButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(44.dp)
            .shadow(5.dp, CircleShape)
            .background(Color.White, CircleShape)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) MaterialTheme.colorScheme.primary else Color.Gray
        )
    }
}

@Composable
fun StatusIcon(status: GameStatus) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .background(Color.White, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (status == GameStatus.PREVIEW) {
            Icon(
                imageVector = Icons.Filled.Settings, // Eye replacement (using Settings as placeholder or find Eye)
                contentDescription = null,
                tint = Color(0xFF3F51B5) // Indigo
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Settings, // Grid 2x2 replacement
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
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
