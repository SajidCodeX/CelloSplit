package com.cellosplit.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.cellosplit.app.ui.theme.BottomSheetShape

@Composable
fun WhiteSheet(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(BottomSheetShape)
            // Primary color in dark mode is setup as #FFFFFF
            .background(MaterialTheme.colorScheme.primary) 
    ) {
        content()
    }
}
