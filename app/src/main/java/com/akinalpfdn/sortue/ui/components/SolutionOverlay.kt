package com.akinalpfdn.sortue.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akinalpfdn.sortue.R
import com.akinalpfdn.sortue.models.Tile

@Composable
fun SolutionOverlay(tiles: List<Tile>, gridDimension: Int) {
    // Sort tiles by correctId to reconstruct the solved image
    val solvedTiles = remember(tiles) { tiles.sortedBy { it.correctId } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { }, // Block touches
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        )
        {
            Text(
                text = stringResource(R.string.target_gradient),
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.shadow(4.dp)
            )

            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
            ){
            LazyVerticalGrid(
                columns = GridCells.Fixed(gridDimension),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                userScrollEnabled = false,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                itemsIndexed(solvedTiles) { _, tile ->
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(tile.rgb.color)
                    ) {
                        if (tile.isFixed) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(4.dp)
                                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
                            )
                        }
                    }

                }
            }
        }
        }
    }
}
