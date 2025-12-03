package com.akinalpfdn.sortue

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.akinalpfdn.sortue.ui.theme.SortueTheme
import com.akinalpfdn.sortue.utils.AudioManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SortueTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SortueApp()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AudioManager.getInstance(this).stopBackgroundMusic()
    }
}