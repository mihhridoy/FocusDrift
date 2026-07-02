package com.radonshadow.focusdrift.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val FocusDriftColorScheme = darkColorScheme(
    primary = IndigoPrimary,
    onPrimary = Background,
    secondary = AmberReward,
    onSecondary = Background,
    tertiary = TealRooms,
    onTertiary = Background,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = Border,
    error = RedDrift,
    onError = Background
)

@Composable
fun FocusDriftTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FocusDriftColorScheme,
        typography = FocusDriftTypography,
        shapes = FocusDriftShapes,
        content = content
    )
}
