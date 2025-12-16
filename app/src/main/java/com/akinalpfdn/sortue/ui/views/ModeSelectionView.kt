package com.akinalpfdn.sortue.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
            Text(
                text = stringResource(R.string.app_name).uppercase(),
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Black,
                color = Color.Black.copy(alpha = 0.8f),
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
                GameMode.CASUAL -> "Relax. No limits. Unlimited hints."
                GameMode.LADDER -> "Climb the ladder. 200 moves limit per level."
                GameMode.CHALLENGE -> "Hardcore. Coming soon."
            }
            Text(
                text = desc,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(horizontal = 40.dp)
                    .height(50.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Play Button
            Button(
                onClick = {
                    // Start game with selected mode.
                    // Grid size defaults to 4 for now in logic, Casual can change it in settings.
                    // But wait, if I had a saved game with size 6, playOrResumeGame should handle it?
                    // Implementation note: `playOrResumeGame` takes size argument.
                    // If resuming, it checks if size matches.
                    // If I pass 4 here, but I have a saved game of size 6, persistence logic says:
                    // if (hasActive && isSameMode && isSameSize) -> resume.
                    // So if I pass 4, and saved is 6, it won't resume? It will overwrite with new 4x4 game?
                    // That's BAD.
                    // FIX: `playOrResumeGame` should probably take `Int?` for size or I need to know the saved size.
                    // OR `ModeSelectionView` doesn't pick size anymore.
                    // If `GameViewModel` knows the saved size, I should just say "Play this Mode".
                    // I'll update `playOrResumeGame` to use `savedSize` if available?
                    // Actually, if I just want to "Resume or New Default", I should pass the size I WANT to start new with.
                    // What if I want to resume my 6x6 game? 
                    // If I pass 4, it will overwrite.
                    // User requirement: "resume game" is gone. Play button handles it.
                    // "we will hold the three modes state all the time"
                    // So if I have a Casual 6x6 state, and I click Play Casual, it MUST resume 6x6.
                    // So `playOrResumeGame` logic must be updated to ignore size mismatch if resuming?
                    // Yes. Detailed fix in next step.
                    // For now, I pass 4 as default "New Game" size if no save exists.
                    onStartGame(currentMode, 4) 
                },
                modifier = Modifier
                    .size(80.dp)
                    .shadow(
                        elevation = 20.dp, 
                        shape = CircleShape, 
                        spotColor = Color(0xFF3F51B5).copy(alpha = 0.5f)
                    ),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
