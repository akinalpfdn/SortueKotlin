package com.akinalpfdn.sortue.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akinalpfdn.sortue.R

@Composable
fun AboutOverlay(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        // Content Card
        Column(
            modifier = Modifier
                .padding(32.dp)
                .shadow(30.dp, RoundedCornerShape(32.dp))
                .clip(RoundedCornerShape(32.dp))
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)) // Approximate ultraThinMaterial
                .border(1.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(32.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Icon
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFC0CB).copy(alpha = 0.1f)) // Pink opacity 0.1
                )
                
                // Gradient Icon
                // Compose doesn't support gradient text/icon easily without custom drawing, 
                // so we'll use a solid color for now or a simple tint.
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp),
                    tint = Color(0xFFFF4081) // Pink Accent
                )
            }

            Text(
                text = stringResource(R.string.thanks_playing),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = stringResource(R.string.hope_enjoy),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Text(
                    text = stringResource(R.string.simple_experience),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Text(
                    text = stringResource(R.string.support_me),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )

                Text(
                    text = stringResource(R.string.music_credit),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Close Button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(top = 16.dp)
                    .shadow(10.dp, CircleShape),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = MaterialTheme.colorScheme.surface
                ),
                shape = CircleShape
            ) {
                Text(
                    text = stringResource(R.string.close),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
