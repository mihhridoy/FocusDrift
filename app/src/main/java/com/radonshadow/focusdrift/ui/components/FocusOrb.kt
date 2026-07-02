package com.radonshadow.focusdrift.ui.components

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.radonshadow.focusdrift.domain.model.OrbSkin
import com.radonshadow.focusdrift.ui.theme.TealRooms

enum class FocusOrbState { IDLE, ACTIVE, DRIFTING, BREAK, COMPLETE }

private data class OrbPalette(val colors: List<Color>, val glow: Color, val glowIntensity: Float)

private fun paletteFor(state: FocusOrbState, orbSkin: OrbSkin): OrbPalette {
    val skinPrimary = Color(android.graphics.Color.parseColor(orbSkin.primaryHex))
    val skinGlow = Color(android.graphics.Color.parseColor(orbSkin.glowHex))

    return when (state) {
        FocusOrbState.IDLE -> OrbPalette(
            colors = listOf(skinPrimary.lighten(0.25f), skinPrimary, skinPrimary.darken(0.35f)),
            glow = skinGlow,
            glowIntensity = 0.5f
        )
        FocusOrbState.ACTIVE -> OrbPalette(
            colors = listOf(skinPrimary.lighten(0.25f), skinPrimary, skinPrimary.darken(0.35f)),
            glow = skinGlow,
            glowIntensity = 0.75f
        )
        FocusOrbState.DRIFTING -> OrbPalette(
            colors = listOf(
                skinPrimary.lighten(0.45f).copy(alpha = 0.55f),
                skinPrimary.lighten(0.1f).copy(alpha = 0.55f),
                skinPrimary.darken(0.2f).copy(alpha = 0.55f)
            ),
            glow = skinPrimary,
            glowIntensity = 0.15f
        )
        FocusOrbState.BREAK -> OrbPalette(
            colors = listOf(TealRooms.lighten(0.25f), TealRooms, TealRooms.darken(0.4f)),
            glow = TealRooms,
            glowIntensity = 0.6f
        )
        FocusOrbState.COMPLETE -> OrbPalette(
            colors = listOf(
                Color(0xFFFFD98A),
                Color(0xFFF4A732),
                Color(0xFFB06F13)
            ),
            glow = Color(0xFFF4A732),
            glowIntensity = 0.8f
        )
    }
}

private fun Color.lighten(amount: Float): Color {
    return Color(
        red = (red + (1f - red) * amount).coerceIn(0f, 1f),
        green = (green + (1f - green) * amount).coerceIn(0f, 1f),
        blue = (blue + (1f - blue) * amount).coerceIn(0f, 1f),
        alpha = alpha
    )
}

private fun Color.darken(amount: Float): Color {
    return Color(
        red = (red * (1f - amount)).coerceIn(0f, 1f),
        green = (green * (1f - amount)).coerceIn(0f, 1f),
        blue = (blue * (1f - amount)).coerceIn(0f, 1f),
        alpha = alpha
    )
}

/**
 * The emotional centerpiece of the app. Pulses gently when idle, tightens and intensifies while a
 * focus session runs, dims when the user is drifting, and glows warm amber on completion.
 */
@Composable
fun FocusOrb(
    state: FocusOrbState,
    modifier: Modifier = Modifier,
    size: Dp = 280.dp,
    orbSkin: OrbSkin = OrbSkin.DEFAULT,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val palette = paletteFor(state, orbSkin)
    val infiniteTransition = rememberInfiniteTransition(label = "orb")

    val animationDurationMs = if (state == FocusOrbState.ACTIVE) 4000 else 3500
    val pulseTarget = if (state == FocusOrbState.ACTIVE) 0.965f else 1.055f
    val pulseBase = if (state == FocusOrbState.ACTIVE) 1f else 1f

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = pulseBase,
        targetValue = pulseTarget,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDurationMs, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbPulse"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = palette.glowIntensity * 0.6f,
        targetValue = palette.glowIntensity,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDurationMs, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orbGlow"
    )

    val appliedScale = if (state == FocusOrbState.DRIFTING) 1f else pulseScale

    Box(modifier = modifier.size(size * 1.3f), contentAlignment = Alignment.Center) {
        repeat(3) { layer ->
            Box(
                modifier = Modifier
                    .size(size + (layer * (size.value * 0.08f)).dp)
                    .scale(appliedScale)
                    .alpha(glowAlpha / (layer + 1))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(palette.glow.copy(alpha = 0.5f), Color.Transparent)
                        ),
                        shape = androidx.compose.foundation.shape.CircleShape
                    )
            )
        }

        Box(
            modifier = Modifier
                .size(size)
                .scale(appliedScale)
                .background(
                    brush = Brush.radialGradient(colors = palette.colors),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}
