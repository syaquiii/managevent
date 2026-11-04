package com.managevent.app.core.theme

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp), // rounded.sm
    small = RoundedCornerShape(8.dp),      // rounded.DEFAULT
    medium = RoundedCornerShape(12.dp),    // Input & Buttons (12dp)
    large = RoundedCornerShape(16.dp),     // Cards & FAB (16dp)
    extraLarge = RoundedCornerShape(28.dp) // Dialogs & BottomSheets (28dp)
)

val CardShape = RoundedCornerShape(16.dp)
val InputShape = RoundedCornerShape(12.dp)
val ButtonShape = RoundedCornerShape(12.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp, bottomStart = 0.dp, bottomEnd = 0.dp)
