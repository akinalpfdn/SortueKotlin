package com.akinalpfdn.sortue.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.akinalpfdn.sortue.R
import com.akinalpfdn.sortue.utils.AudioManager

@Composable
fun SettingsOverlay(
    onDismiss: () -> Unit,
    isHapticsEnabled: Boolean,
    onHapticsChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val audioManager = remember { AudioManager.getInstance(context) }
    var isMusicEnabled by remember { mutableStateOf(audioManager.isMusicEnabled) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onDismiss() }
            .zIndex(200f), // Ensure high z-index
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {} // Prevent dismiss
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.music),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Switch(
                    checked = isMusicEnabled,
                    onCheckedChange = {
                        isMusicEnabled = it
                        audioManager.isMusicEnabled = it
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF3F51B5),
                        checkedTrackColor = Color(0xFF3F51B5).copy(alpha = 0.5f)
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.vibration), // Hardcoded for speed, can i18n later if asked
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Switch(
                    checked = isHapticsEnabled,
                    onCheckedChange = onHapticsChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF3F51B5),
                        checkedTrackColor = Color(0xFF3F51B5).copy(alpha = 0.5f)
                    )
                )
            }
        }
    }
}
