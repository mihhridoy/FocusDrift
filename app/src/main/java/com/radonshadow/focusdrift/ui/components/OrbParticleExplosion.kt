package com.radonshadow.focusdrift.ui.components

import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.radonshadow.focusdrift.ui.theme.AmberReward
import com.radonshadow.focusdrift.ui.theme.GreenSuccess
import com.radonshadow.focusdrift.ui.theme.IndigoPrimary
import com.radonshadow.focusdrift.ui.theme.TealRooms
import kotlin.math.cos
import kotlin.math.sin

private data class Particle(val angleDeg: Float, val distanceDp: Float, val color: Color, val sizeDp: Float, val delayMs: Int)

private val PARTICLES = listOf(
    Particle(220f, 88f, AmberReward, 10f, 0),
    Particle(315f, 92f, IndigoPrimary, 8f, 200),
    Particle(35f, 96f, TealRooms, 9f, 100),
    Particle(150f, 90f, AmberReward, 7f, 300),
    Particle(270f, 100f, GreenSuccess, 8f, 150)
)

/** Bursts colored dots outward from the orb's center — plays on a loop while the win screen is shown. */
@Composable
fun OrbParticleExplosion(modifier: Modifier = Modifier, size: Dp = 180.dp) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        PARTICLES.forEach { particle -> ParticleDot(particle) }
    }
}

@Composable
private fun ParticleDot(particle: Particle) {
    val transition = rememberInfiniteTransition(label = "particle")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, delayMillis = particle.delayMs, easing = EaseOutQuart),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleProgress"
    )

    val radians = Math.toRadians(particle.angleDeg.toDouble())
    val dx = (cos(radians) * particle.distanceDp * progress).toFloat()
    val dy = (sin(radians) * particle.distanceDp * progress).toFloat()

    Box(
        modifier = Modifier
            .offset(x = dx.dp, y = dy.dp)
            .size(particle.sizeDp.dp)
            .scale(1f - progress * 0.7f)
            .alpha(1f - progress)
            .background(particle.color, CircleShape)
    )
}
