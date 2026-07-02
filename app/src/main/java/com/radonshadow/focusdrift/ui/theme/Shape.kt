package com.radonshadow.focusdrift.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val CardShape = RoundedCornerShape(20.dp)
val CardShapeLarge = RoundedCornerShape(24.dp)
val ChipShape = RoundedCornerShape(14.dp)
val PillShape = RoundedCornerShape(99.dp)
val OrbShape = RoundedCornerShape(percent = 50)

val FocusDriftShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = ChipShape,
    medium = CardShape,
    large = CardShapeLarge,
    extraLarge = PillShape
)
