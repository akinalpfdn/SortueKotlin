package com.akinalpfdn.sortue.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.graphics.TileMode
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.width
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akinalpfdn.sortue.R
import com.akinalpfdn.sortue.models.GameMode
import com.akinalpfdn.sortue.ui.components.AmbientBackground
import com.akinalpfdn.sortue.ui.components.WheelPicker

@Composable
fun ModeSelectionView(
    onStartGame: (GameMode, Int) -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit
) {
    val modes = GameMode.values().toList()
    var selectedModeIndex by remember { mutableIntStateOf(0) } // Default to 0 (Casual)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Shared Background
        AmbientBackground()

        // Top Bar Area: Menu Button (Matching GameView style/location)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 24.dp), // Exact padding from GameView
            contentAlignment = Alignment.TopStart
        ) {
             var showMenu by remember { androidx.compose.runtime.mutableStateOf(false) }

             Box {
                 androidx.compose.material3.IconButton(
                     onClick = { showMenu = true },
                     modifier = Modifier.size(44.dp)
                 ) {
                     // Replicating StatusIcon style for "Settings/Menu" visual
                     Box(
                        modifier = Modifier
                            .size(44.dp)
                            .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = Color.Black.copy(alpha = 0.05f))
                            .background(Color.White, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.Settings,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                 }

                 androidx.compose.material3.DropdownMenu(
                     expanded = showMenu,
                     onDismissRequest = { showMenu = false },
                     modifier = Modifier.background(Color.White)
                 ) {
                     androidx.compose.material3.DropdownMenuItem(
                         text = { Text(stringResource(R.string.settings)) },
                         onClick = {
                             showMenu = false
                             onSettingsClick()
                         }
                     )
                     androidx.compose.material3.DropdownMenuItem(
                         text = { Text(stringResource(R.string.about)) },
                         onClick = {
                             showMenu = false
                             onAboutClick()
                         }
                     )
                 }
             }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 48.dp)
        ) {
            // App Title
           val rainbowColors = listOf(
    Color(0xFFFF8080), // Saturated Pastel Red (Salmon-ish)
    Color(0xFFFFB347), // Saturated Pastel Orange
    Color(0xFFFFE066), // Saturated Pastel Yellow (Darker to be visible)
    Color(0xFF77DD77), // Saturated Pastel Green
    Color(0xFF779ECB), // Saturated Pastel Blue
    Color(0xFF966FD6), // Saturated Pastel Indigo
    Color(0xFFC3B1E1)  // Saturated Pastel Violet
)
            
            Text(
                text = stringResource(R.string.app_name).uppercase(),
                style = MaterialTheme.typography.displayMedium.copy(
                    brush = Brush.horizontalGradient(colors = rainbowColors)
                ),
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                modifier = Modifier.padding(top = 20.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Vertical Wheel Picker for Modes
            // "Vertical wheel slider"
            WheelPicker(
                items = modes,
                initialIndex = 0,
                visibleItemsCount = 3, // Changed from 5 to 3 for compactness
                itemHeight = 50.dp, // Slightly smaller item height
                onSelectionChanged = { selectedModeIndex = it }
            ) { mode, isSelected ->
                 // Because WheelPicker now handles scaling in graphicsLayer, we just provide base text.
                 // The "isSelected" param is still useful for color/weight changes.
                Text(
                    text = mode.name,
                    style = MaterialTheme.typography.titleLarge, // Base size
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.Black else Color.Gray,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))

            // Description of current mode
            val currentMode = modes.getOrElse(selectedModeIndex) { GameMode.CASUAL }
            val desc = when(currentMode) {
                GameMode.CASUAL -> stringResource(R.string.casual_Text)
                GameMode.PRECISION -> stringResource(R.string.ladder_Text)
                GameMode.PURE -> stringResource(R.string.challenge_Text)
            }
            Text(
                text = desc,
                style = MaterialTheme.typography.bodyLarge,
                color = if (currentMode == GameMode.PURE) Color(0xFFFF8080) else Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .height(50.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Play Button (Mosaic Triangle)
            MosaicPlayButton(
                onClick = { onStartGame(currentMode, 4) },
                colors = rainbowColors
            )

            Spacer(modifier = Modifier.padding(bottom = 48.dp))
        }
    }
}

@Composable
fun MosaicPlayButton(
    onClick: () -> Unit,
    colors: List<Color>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(80.dp)
            .height(100.dp)
            // Use a custom shape that looks like a Play button (Triangle)
            .shadow(16.dp, TriangleShape, spotColor = colors.first().copy(alpha = 0.5f))
            .clip(TriangleShape)
            .background(Color.White) // Background behind tiles
            .clickable(onClick = onClick)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val rows = 5 // Reduced from 8
            val cols = 5 // Adjusted to match aspect ratio approximately
            
            val tileW = size.width / cols
            val tileH = size.height / rows
            
            for (row in 0 until rows) {
                for (col in 0 until cols) {
                    val color = colors.random()
                    
                    // Draw rect for each "tile"
                    drawRect(
                        color = color,
                        topLeft = Offset(col * tileW, row * tileH),
                        size = Size(tileW, tileH)
                    )
                    
                    // Optional: Inner border effect (white stroke to separate tiles)
                     drawRect(
                        color = Color.White,
                        topLeft = Offset(col * tileW, row * tileH),
                        size = Size(tileW, tileH),
                        style = Stroke(width = 2f)
                    )
                }
            }
        }
    }
}

// Shape Definition
private val TriangleShape = GenericShape { size, _ ->
    moveTo(0f, 0f)
    lineTo(size.width, size.height / 2f)
    lineTo(0f, size.height)
    close()
}
