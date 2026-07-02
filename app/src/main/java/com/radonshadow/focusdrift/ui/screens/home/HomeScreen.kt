package com.radonshadow.focusdrift.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.radonshadow.focusdrift.core.extensions.clickableNoRipple
import com.radonshadow.focusdrift.core.extensions.toHoursMinutesLabel
import com.radonshadow.focusdrift.core.extensions.minutesToMillis
import com.radonshadow.focusdrift.ui.components.QuickStatCard
import com.radonshadow.focusdrift.ui.components.XpProgressBar
import com.radonshadow.focusdrift.ui.theme.AmberReward
import com.radonshadow.focusdrift.ui.theme.Background
import com.radonshadow.focusdrift.ui.theme.Border
import com.radonshadow.focusdrift.ui.theme.IndigoPrimary
import com.radonshadow.focusdrift.ui.theme.Nunito
import com.radonshadow.focusdrift.ui.theme.Surface
import com.radonshadow.focusdrift.ui.theme.TealRooms
import com.radonshadow.focusdrift.ui.theme.TextPrimary
import com.radonshadow.focusdrift.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    onStartFocus: () -> Unit,
    onJoinRooms: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp)
            .padding(top = 10.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopBar(level = uiState.userProgress.level, xpIntoLevel = uiState.userProgress.xpIntoLevel, xpToNextLevel = uiState.userProgress.xpToNextLevel)
        GoalCard(
            sessionsToday = uiState.userProgress.sessionsToday,
            dailyGoal = uiState.userProgress.dailyGoalSessions,
            onStartFocus = onStartFocus
        )

        if (uiState.hasActiveRoom) {
            ActiveRoomBanner(participantCount = uiState.activeRoomParticipantCount, onJoin = onJoinRooms)
        }

        if (uiState.habitStreaks.isNotEmpty()) {
            Column {
                Text("HABIT STREAKS", color = TextSecondary, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.habitStreaks) { summary ->
                        HabitStreakChip(summary)
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickStatCard(
                icon = Icons.Filled.LocalFireDepartment,
                label = "Streak",
                value = "${uiState.userProgress.currentStreak}d",
                valueColor = AmberReward,
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                icon = Icons.Filled.Whatshot,
                label = "XP today",
                value = "${uiState.userProgress.xpEarnedToday}",
                valueColor = IndigoPrimary,
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                icon = Icons.Filled.Timer,
                label = "Focus",
                value = uiState.userProgress.focusMinutesToday.minutesToMillis().toHoursMinutesLabel(),
                valueColor = TealRooms,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TopBar(level: Int, xpIntoLevel: Int, xpToNextLevel: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(IndigoPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Person, contentDescription = null, tint = Background)
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(AmberReward, RoundedCornerShape(99.dp))
                    .padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
                Text("Lv.$level", color = Background, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 10.sp)
            }
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Good focus day", color = TextPrimary, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp)
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = AmberReward, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                XpProgressBar(
                    progress = if (xpToNextLevel > 0) xpIntoLevel.toFloat() / xpToNextLevel else 0f,
                    modifier = Modifier.weight(1f),
                    height = 7.dp
                )
                Spacer(Modifier.width(8.dp))
                Text("$xpIntoLevel/$xpToNextLevel XP", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun GoalCard(sessionsToday: Int, dailyGoal: Int, onStartFocus: () -> Unit) {
    val progress = if (dailyGoal > 0) (sessionsToday.toFloat() / dailyGoal).coerceIn(0f, 1f) else 0f
    val remaining = (dailyGoal - sessionsToday).coerceAtLeast(0)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .goalCardRing(progress)
            .padding(3.dp)
            .background(Surface, RoundedCornerShape(22.dp))
            .padding(22.dp)
    ) {
        Column {
            Text("TODAY'S FOCUS GOAL", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("$sessionsToday", color = TextPrimary, fontFamily = com.radonshadow.focusdrift.ui.theme.DmMono, fontSize = 44.sp, fontWeight = FontWeight.Medium)
                Text(" / $dailyGoal sessions", color = TextSecondary, fontFamily = com.radonshadow.focusdrift.ui.theme.DmMono, fontSize = 24.sp)
            }
            if (remaining > 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$remaining more to hit your streak", color = AmberReward, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = AmberReward, modifier = Modifier.size(16.dp))
                }
            } else {
                Text("Daily goal hit — nice work", color = AmberReward, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(Modifier.height(18.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .background(IndigoPrimary, RoundedCornerShape(99.dp))
                    .clickableNoRipple(onStartFocus),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = Background)
                Spacer(Modifier.width(8.dp))
                Text("Start Focus", color = Background, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }
        }
    }
}

/** Draws the thin progress ring that traces the goal card's rounded-rect border. */
private fun Modifier.goalCardRing(progress: Float): Modifier = this.then(
    Modifier
        .padding(3.dp)
        .drawBehind {
            val strokeWidth = 3.dp.toPx()
            val corner = 22.dp.toPx()
            val inset = strokeWidth / 2
            val outline = androidx.compose.ui.graphics.Path().apply {
                addRoundRect(
                    androidx.compose.ui.geometry.RoundRect(
                        left = inset,
                        top = inset,
                        right = size.width - inset,
                        bottom = size.height - inset,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner, corner)
                    )
                )
            }
            drawPath(outline, color = Border, style = Stroke(width = strokeWidth))
            if (progress > 0f) {
                val measure = androidx.compose.ui.graphics.PathMeasure().apply { setPath(outline, false) }
                val segment = androidx.compose.ui.graphics.Path()
                measure.getSegment(0f, measure.length * progress.coerceIn(0f, 1f), segment, true)
                drawPath(segment, color = IndigoPrimary, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
            }
        }
)

@Composable
private fun ActiveRoomBanner(participantCount: Int, onJoin: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(20.dp))
            .clickableNoRipple(onJoin)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        com.radonshadow.focusdrift.ui.components.ParticipantAvatarCluster(
            colors = listOf(TealRooms, IndigoPrimary, AmberReward, com.radonshadow.focusdrift.ui.theme.GreenSuccess),
            avatarSize = 34.dp
        )
        Spacer(Modifier.width(14.dp))
        Column {
            Text("$participantCount people focusing right now", color = TextPrimary, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Join them →", color = TealRooms, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

@Composable
private fun HabitStreakChip(summary: HabitStreakSummary) {
    Column(
        modifier = Modifier
            .background(Surface, RoundedCornerShape(18.dp))
            .padding(14.dp)
    ) {
        Text(summary.habit.emoji, fontSize = 22.sp)
        Spacer(Modifier.height(6.dp))
        Text(summary.habit.name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(Modifier.height(2.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = AmberReward, modifier = Modifier.size(14.dp))
            Text(" ${summary.currentStreak}", color = AmberReward, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}
