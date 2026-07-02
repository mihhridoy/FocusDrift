package com.radonshadow.focusdrift.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radonshadow.focusdrift.core.extensions.clickableNoRipple
import com.radonshadow.focusdrift.domain.model.Habit
import com.radonshadow.focusdrift.domain.model.StreakInfo
import com.radonshadow.focusdrift.ui.theme.Background
import com.radonshadow.focusdrift.ui.theme.Border
import com.radonshadow.focusdrift.ui.theme.GreenSuccess
import com.radonshadow.focusdrift.ui.theme.IndigoPrimary
import com.radonshadow.focusdrift.ui.theme.Inter
import com.radonshadow.focusdrift.ui.theme.Nunito
import com.radonshadow.focusdrift.ui.theme.RedDrift
import com.radonshadow.focusdrift.ui.theme.Surface
import com.radonshadow.focusdrift.ui.theme.TextPrimary

@Composable
fun HabitCard(
    habit: Habit,
    streakInfo: StreakInfo,
    onMarkDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tint = when {
        streakInfo.isPaused -> RedDrift
        streakInfo.isCompletedToday -> GreenSuccess
        else -> IndigoPrimary
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(listOf(tint.copy(alpha = 0.14f), Surface)),
                RoundedCornerShape(20.dp)
            )
            .border(1.dp, Border, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = habit.emoji, fontSize = 28.sp)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(habit.name, color = TextPrimary, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                if (streakInfo.isPaused) {
                    Text(
                        "Streak paused — not broken. Resume today.",
                        color = RedDrift,
                        fontFamily = Inter,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                } else {
                    StreakBadge(days = streakInfo.currentStreak, size = StreakBadgeSize.SMALL)
                }
            }
        }

        if (!streakInfo.isPaused) {
            Spacer(Modifier.height(14.dp))
            WeekDotRow(completion = streakInfo.weekCompletion, filledColor = tint)
            Spacer(Modifier.height(14.dp))

            if (streakInfo.isCompletedToday) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .background(GreenSuccess, RoundedCornerShape(99.dp)),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Done today", color = Background, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Background, modifier = Modifier.padding(start = 6.dp))
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .border(1.dp, IndigoPrimary, RoundedCornerShape(99.dp))
                        .clickableNoRipple(onMarkDone),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mark done today", color = IndigoPrimary, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                }
            }
        }
    }
}
