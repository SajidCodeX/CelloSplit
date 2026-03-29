package com.cellosplit.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

val BottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
val PillShape = RoundedCornerShape(50) // 50% for fully rounded pills
