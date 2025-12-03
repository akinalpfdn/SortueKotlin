package com.akinalpfdn.sortue.ui.components

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@Composable
fun RateOverlay(
    onRate: () -> Unit,
    onRemind: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.2f)) // Softer dim
            .clickable(enabled = false) {}, // Block clicks behind
        contentAlignment = Alignment.Center
    ) {
        // Premium Glass Card
        Column(
            modifier = Modifier
                .padding(32.dp)
                // Premium shadow with spot color
                .shadow(
                    elevation = 30.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = Color.Black.copy(alpha = 0.25f)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White.copy(alpha = 0.95f)) // Glass effect
                .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(32.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(30.dp)
        ) {
            // Icon Glow Container
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
                                    Color.Yellow.copy(alpha = 0.2f),
                                    Color(0xFFFFA500).copy(alpha = 0.2f)
                                )
                            ),
                            shape = CircleShape
                        )
                )

                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = Color(0xFFFFA500) // Orange
                )
            }

            // Typography
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.enjoying_sortue),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Serif, // Premium font
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = Color.Black
                )

                Text(
                    text = stringResource(R.string.rate_message),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }

            // Actions
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(horizontal = 10.dp)
            ) {
                // Primary Action (Rate Now) - Custom Gradient Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        // Pink Glow Shadow
                        .shadow(
                            elevation = 10.dp,
                            shape = CircleShape,
                            spotColor = Color(0xFFFF4081).copy(alpha = 0.4f)
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFF4081), Color(0xFF9C27B0)) // Pink to Purple
                            )
                        )
                        .clickable(onClick = onRate),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.rate_now),
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Secondary Action (Remind Me Later)
                TextButton(
                    onClick = onRemind,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(
                        text = stringResource(R.string.remind_later),
                        fontWeight = FontWeight.Medium,
                        color = Color.Black.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}