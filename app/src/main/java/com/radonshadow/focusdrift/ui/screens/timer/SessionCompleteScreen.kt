package com.radonshadow.focusdrift.ui.screens.timer

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.radonshadow.focusdrift.core.extensions.clickableNoRipple
import com.radonshadow.focusdrift.core.extensions.toMinutes
import com.radonshadow.focusdrift.domain.model.SessionState
import com.radonshadow.focusdrift.ui.components.FocusOrb
import com.radonshadow.focusdrift.ui.components.FocusOrbState
import com.radonshadow.focusdrift.ui.components.OrbParticleExplosion
import com.radonshadow.focusdrift.ui.components.SessionRewardCard
import com.radonshadow.focusdrift.ui.theme.AmberReward
import com.radonshadow.focusdrift.ui.theme.Background
import com.radonshadow.focusdrift.ui.theme.GreenSuccess
import com.radonshadow.focusdrift.ui.theme.IndigoPrimary
import com.radonshadow.focusdrift.ui.theme.Nunito
import com.radonshadow.focusdrift.ui.theme.TealRooms
import com.radonshadow.focusdrift.ui.theme.TextPrimary
import com.radonshadow.focusdrift.ui.theme.TextSecondary

@Composable
fun SessionCompleteScreen(
    onDone: () -> Unit,
    viewModel: TimerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val complete = uiState.sessionState as? SessionState.Complete ?: run {
        onDone()
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        ConfettiLayer(modifier = Modifier.fillMaxSize())

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                FocusOrb(state = FocusOrbState.COMPLETE, size = 120.dp)
                OrbParticleExplosion(size = 180.dp)
            }

            Spacer(Modifier.height(24.dp))
            Text("Session Complete! 🎉", color = TextPrimary, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 30.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text("You focused for ${complete.focusDurationMs.toMinutes()} minutes", color = TextSecondary, fontSize = 15.sp)

            Spacer(Modifier.height(24.dp))
            val (_, xpIntoLevel, xpToNextLevel) = com.radonshadow.focusdrift.core.constants.RewardConstants.levelProgressFor(complete.xpAfter)
            val xpProgress = if (xpToNextLevel > 0) xpIntoLevel.toFloat() / xpToNextLevel else 0f
            SessionRewardCard(
                xpEarned = complete.xpEarned,
                xpBefore = complete.xpBefore,
                xpAfter = complete.xpAfter,
                xpProgress = xpProgress.coerceIn(0f, 1f),
                coinsEarned = complete.coinsEarned,
                currentStreak = complete.currentStreak
            )

            Spacer(Modifier.height(30.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .background(Background, RoundedCornerShape(99.dp))
                        .border(1.5.dp, TealRooms, RoundedCornerShape(99.dp))
                        .clickableNoRipple { viewModel.startBreak(uiState.nextSessionNumber); onDone() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Take a break", color = TealRooms, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .background(IndigoPrimary, RoundedCornerShape(99.dp))
                        .clickableNoRipple { viewModel.keepGoing(); onDone() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("Keep going", color = Background, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                }
            }
        }
    }
}

private val CONFETTI_COLORS = listOf(AmberReward, IndigoPrimary, TealRooms, GreenSuccess)

@Composable
private fun ConfettiLayer(modifier: Modifier = Modifier) {
    androidx.compose.foundation.layout.BoxWithConstraints(modifier = modifier) {
        val fallDistance = maxHeight + 60.dp
        val positions = listOf(0.12f, 0.28f, 0.44f, 0.62f, 0.78f, 0.88f)
        positions.forEachIndexed { index, xFraction ->
            ConfettiStrip(
                x = maxWidth * xFraction,
                fallDistance = fallDistance,
                color = CONFETTI_COLORS[index % CONFETTI_COLORS.size],
                durationMs = 2200 + index * 120,
                delayMs = index * 150
            )
        }
    }
}

@Composable
private fun ConfettiStrip(x: androidx.compose.ui.unit.Dp, fallDistance: androidx.compose.ui.unit.Dp, color: Color, durationMs: Int, delayMs: Int) {
    val transition = rememberInfiniteTransition(label = "confetti")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(durationMs, delayMillis = delayMs, easing = LinearEasing), RepeatMode.Restart),
        label = "confettiFall"
    )

    Box(
        modifier = Modifier
            .offset(x = x, y = fallDistance * progress - 40.dp)
            .size(width = 8.dp, height = 14.dp)
            .background(color, RoundedCornerShape(2.dp))
    )
}
