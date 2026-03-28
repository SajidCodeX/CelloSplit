package com.cellosplit.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * Temporary root composable — will be replaced in Chunk 3c with the
 * full NavHost, floating pill nav, and all screens.
 */
@Composable
fun CelloSplitRoot() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "CelloSplit — M2 complete ✅", color = Color.White)
    }
}
