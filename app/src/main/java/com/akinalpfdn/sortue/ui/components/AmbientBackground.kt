package com.akinalpfdn.sortue.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
@Composable
fun AmbientBackground() {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Swift: Color.white.ignoresSafeArea()
    ) {
        val center = this.center

        // Swift: Circle().fill(Color.purple.opacity(0.2)).blur(radius: 60).offset(x: -100, y: -200)
        val purpleColor = Color(0xFFAF52DE) // iOS System Purple
        val purpleOffset = Offset(
            x = center.x - 100.dp.toPx(),
            y = center.y - 200.dp.toPx()
        )

        // Simulating blur using a Radial Gradient
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(purpleColor.copy(alpha = 0.2f), Color.Transparent),
                center = purpleOffset,
                radius = 210.dp.toPx() // 150dp radius + 60dp blur approx
            ),
            radius = 210.dp.toPx(),
            center = purpleOffset
        )

        // Swift: Circle().fill(Color.blue.opacity(0.2)).blur(radius: 60).offset(x: 100, y: 300)
        val blueColor = Color(0xFF007AFF) // iOS System Blue
        val blueOffset = Offset(
            x = center.x + 100.dp.toPx(),
            y = center.y + 300.dp.toPx()
        )

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(blueColor.copy(alpha = 0.2f), Color.Transparent),
                center = blueOffset,
                radius = 210.dp.toPx()
            ),
            radius = 210.dp.toPx(),
            center = blueOffset
        )
    }
}
