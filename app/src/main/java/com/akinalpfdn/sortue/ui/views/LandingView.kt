package com.akinalpfdn.sortue.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akinalpfdn.sortue.R
import com.akinalpfdn.sortue.ui.components.AmbientBackground

@Composable
fun LandingView(onPlay: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        AmbientBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 140.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(1.dp))

            // Title Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    fontSize = 60.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = stringResource(R.string.landing_subtitle),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Tutorial / Info Card (Glassmorphism Style)
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    // Soft shadow
                    .shadow(
                        elevation = 20.dp,
                        shape = RoundedCornerShape(24.dp),
                        spotColor = Color.Black.copy(alpha = 0.1f)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    // Frosted glass effect: High alpha white instead of gray
                    .background(Color.White.copy(alpha = 0.85f))
                    // Subtle white border for "glass edge"
                    .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.TouchApp, // Requires extended icons, cleaner than PlayArrow
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color(0xFF3F51B5) // Indigo
                )

                Text(
                    text = stringResource(R.string.how_to_play_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.how_to_play_desc),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color.Black.copy(alpha = 0.6f),
                    lineHeight = 24.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Play Button (Custom Gradient Implementation)
            Box(
                modifier = Modifier
                    .padding(horizontal = 40.dp, vertical = 40.dp)
                    .fillMaxWidth()
                    .height(60.dp)
                    // Colored shadow matching the gradient (Indigo glow)
                    .shadow(
                        elevation = 15.dp,
                        shape = CircleShape,
                        spotColor = Color(0xFF3F51B5).copy(alpha = 0.5f)
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color(0xFF3F51B5), Color(0xFF9C27B0)) // Indigo -> Purple
                        )
                    )
                    .clickable(onClick = onPlay),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.start_game),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}