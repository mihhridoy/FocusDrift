package com.radonshadow.focusdrift.ui.screens.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.radonshadow.focusdrift.core.extensions.clickableNoRipple
import com.radonshadow.focusdrift.core.extensions.toMMSS
import com.radonshadow.focusdrift.domain.model.FocusSound
import com.radonshadow.focusdrift.domain.model.SessionState
import com.radonshadow.focusdrift.domain.model.SessionType
import com.radonshadow.focusdrift.ui.components.DriftButton
import com.radonshadow.focusdrift.ui.components.FocusOrb
import com.radonshadow.focusdrift.ui.components.FocusOrbState
import com.radonshadow.focusdrift.ui.components.KeepScreenOn
import com.radonshadow.focusdrift.ui.components.SessionControlsBar
import com.radonshadow.focusdrift.ui.theme.AmberReward
import com.radonshadow.focusdrift.ui.theme.Background
import com.radonshadow.focusdrift.ui.theme.DmMono
import com.radonshadow.focusdrift.ui.theme.IndigoPrimary
import com.radonshadow.focusdrift.ui.theme.Nunito
import com.radonshadow.focusdrift.ui.theme.Surface
import com.radonshadow.focusdrift.ui.theme.SurfaceElevated
import com.radonshadow.focusdrift.ui.theme.TextPrimary
import com.radonshadow.focusdrift.ui.theme.TextSecondary

@Composable
fun TimerScreen(
    onSessionComplete: () -> Unit,
    onGoPro: () -> Unit,
    viewModel: TimerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.sessionState) {
        if (uiState.sessionState is SessionState.Complete) onSessionComplete()
    }

    // A focus session easily outlasts the device's screen timeout, which would otherwise dim and
    // lock the screen mid-session — read as the app "going dark" or "closing" while it was
    // actually still running underneath a sleeping display.
    val keepScreenOn = when (uiState.sessionState) {
        is SessionState.Running, is SessionState.Paused, is SessionState.Drifting, is SessionState.OnBreak -> true
        else -> false
    }
    KeepScreenOn(enabled = keepScreenOn)

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        when (val state = uiState.sessionState) {
            is SessionState.Idle -> IdleContent(
                task = uiState.task,
                onTaskChange = viewModel::updateTask,
                onStart = viewModel::startFocusSession,
                focusMinutes = uiState.focusMinutes,
                onFocusMinutesChange = viewModel::setFocusMinutes,
                selectedFocusSoundId = uiState.selectedFocusSoundId,
                focusSoundVolume = uiState.focusSoundVolume,
                onFocusSoundSelected = viewModel::selectFocusSound,
                onFocusSoundVolumeChange = viewModel::setFocusSoundVolume,
                isPro = uiState.isPro,
                sessionsCompletedToday = uiState.sessionsCompletedToday,
                freeSessionCapReached = uiState.freeSessionCapReached,
                onGoPro = onGoPro
            )
            is SessionState.Running -> RunningContent(state, onPauseResume = viewModel::pause, onReset = viewModel::abandon, onCompleteEarly = viewModel::completeEarly, onDrift = viewModel::markDrifting)
            is SessionState.Paused -> PausedContent(state, onPauseResume = viewModel::resume, onReset = viewModel::abandon, onCompleteEarly = viewModel::completeEarly)
            is SessionState.Drifting -> DriftingContent(state, onResume = viewModel::resumeFromDrift)
            is SessionState.OnBreak -> BreakContent(state)
            // Complete is a one-shot snapshot for SessionCompleteScreen; the LaunchedEffect above
            // navigates there immediately. Falling back to the idle screen here (rather than
            // rendering nothing) means a stale/leftover Complete state can never show as a blank
            // Timer tab, even for a single frame.
            is SessionState.Complete -> IdleContent(
                task = uiState.task,
                onTaskChange = viewModel::updateTask,
                onStart = viewModel::startFocusSession,
                focusMinutes = uiState.focusMinutes,
                onFocusMinutesChange = viewModel::setFocusMinutes,
                selectedFocusSoundId = uiState.selectedFocusSoundId,
                focusSoundVolume = uiState.focusSoundVolume,
                onFocusSoundSelected = viewModel::selectFocusSound,
                onFocusSoundVolumeChange = viewModel::setFocusSoundVolume,
                isPro = uiState.isPro,
                sessionsCompletedToday = uiState.sessionsCompletedToday,
                freeSessionCapReached = uiState.freeSessionCapReached,
                onGoPro = onGoPro
            )
        }
    }
}

@Composable
private fun IdleContent(
    task: String,
    onTaskChange: (String) -> Unit,
    onStart: () -> Unit,
    focusMinutes: Int,
    onFocusMinutesChange: (Int) -> Unit,
    selectedFocusSoundId: String,
    focusSoundVolume: Float,
    onFocusSoundSelected: (String) -> Unit,
    onFocusSoundVolumeChange: (Float) -> Unit,
    isPro: Boolean,
    sessionsCompletedToday: Int,
    freeSessionCapReached: Boolean,
    onGoPro: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FocusOrb(state = FocusOrbState.IDLE, size = 220.dp) {
            Text("${focusMinutes.toString().padStart(2, '0')}:00", color = TextPrimary, fontFamily = DmMono, fontSize = 44.sp)
        }
        if (!isPro) {
            Spacer(Modifier.height(14.dp))
            Text(
                "$sessionsCompletedToday / ${com.radonshadow.focusdrift.core.constants.SubscriptionConstants.FREE_SESSIONS_PER_DAY} free sessions today",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(36.dp))
        OutlinedTextField(
            value = task,
            onValueChange = onTaskChange,
            placeholder = { Text("What are you working on?", color = TextSecondary) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = IndigoPrimary,
                unfocusedBorderColor = SurfaceElevated
            ),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))
        DurationPicker(focusMinutes = focusMinutes, onFocusMinutesChange = onFocusMinutesChange)
        Spacer(Modifier.height(24.dp))
        FocusSoundPicker(
            selectedFocusSoundId = selectedFocusSoundId,
            volume = focusSoundVolume,
            onSoundSelected = onFocusSoundSelected,
            onVolumeChange = onFocusSoundVolumeChange
        )
        Spacer(Modifier.height(24.dp))
        if (freeSessionCapReached) {
            Text(
                "Upgrade to Pro for unlimited focus sessions →",
                color = AmberReward,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 10.dp).clickableNoRipple(onGoPro)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    if (freeSessionCapReached) {
                        androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(IndigoPrimary, AmberReward))
                    } else {
                        androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(IndigoPrimary, IndigoPrimary))
                    },
                    RoundedCornerShape(99.dp)
                )
                .clickableNoRipple(if (freeSessionCapReached) onGoPro else onStart),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (freeSessionCapReached) "Unlock unlimited sessions" else "Start Focus",
                color = Background,
                fontFamily = Nunito,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 17.sp
            )
        }
    }
}

@Composable
private fun DurationPicker(focusMinutes: Int, onFocusMinutesChange: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Session length", color = TextSecondary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text("$focusMinutes min", color = IndigoPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Slider(
            value = focusMinutes.toFloat(),
            onValueChange = { onFocusMinutesChange(it.toInt()) },
            valueRange = com.radonshadow.focusdrift.core.constants.TimerConstants.MIN_FOCUS_MINUTES.toFloat()..
                com.radonshadow.focusdrift.core.constants.TimerConstants.MAX_FOCUS_MINUTES.toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = TextPrimary,
                activeTrackColor = IndigoPrimary,
                inactiveTrackColor = SurfaceElevated
            )
        )
    }
}

@Composable
private fun FocusSoundPicker(
    selectedFocusSoundId: String,
    volume: Float,
    onSoundSelected: (String) -> Unit,
    onVolumeChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Background sound",
            color = TextSecondary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 10.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FocusSound.entries.forEach { sound ->
                val selected = sound.id == selectedFocusSoundId
                Box(
                    modifier = Modifier
                        .background(if (selected) IndigoPrimary else Surface, RoundedCornerShape(99.dp))
                        .clickableNoRipple { onSoundSelected(sound.id) }
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                ) {
                    Text(
                        sound.label,
                        color = if (selected) Background else TextSecondary,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }
        if (selectedFocusSoundId != FocusSound.NONE.id) {
            Spacer(Modifier.height(14.dp))
            Slider(
                value = volume,
                onValueChange = onVolumeChange,
                valueRange = 0f..1f,
                colors = SliderDefaults.colors(
                    thumbColor = TextPrimary,
                    activeTrackColor = IndigoPrimary,
                    inactiveTrackColor = SurfaceElevated
                )
            )
        }
    }
}

@Composable
private fun RunningContent(
    state: SessionState.Running,
    onPauseResume: () -> Unit,
    onReset: () -> Unit,
    onCompleteEarly: () -> Unit,
    onDrift: () -> Unit
) {
    SessionScaffold(
        headerLabel = "FOCUS SESSION ${state.sessionNumber} OF ${state.dailyGoalSessions}",
        orbState = FocusOrbState.ACTIVE,
        remainingMs = state.remainingMs,
        totalMs = state.totalMs,
        task = state.currentTask,
        nextLabel = "Next: 5 min break",
        isPaused = false,
        onPauseResume = onPauseResume,
        onReset = onReset,
        onCompleteEarly = onCompleteEarly,
        bottomContent = { DriftButton(isDrifting = false, onClick = onDrift) }
    )
}

@Composable
private fun PausedContent(
    state: SessionState.Paused,
    onPauseResume: () -> Unit,
    onReset: () -> Unit,
    onCompleteEarly: () -> Unit
) {
    SessionScaffold(
        headerLabel = "FOCUS SESSION ${state.sessionNumber} · PAUSED",
        orbState = FocusOrbState.ACTIVE,
        remainingMs = state.remainingMs,
        totalMs = state.totalMs,
        task = state.currentTask,
        nextLabel = "Paused",
        isPaused = true,
        onPauseResume = onPauseResume,
        onReset = onReset,
        onCompleteEarly = onCompleteEarly,
        bottomContent = {}
    )
}

@Composable
private fun DriftingContent(state: SessionState.Drifting, onResume: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        FocusOrb(state = FocusOrbState.DRIFTING, size = 260.dp) {
            Text(state.remainingMs.toMMSS(), color = TextPrimary, fontFamily = DmMono, fontSize = 56.sp)
        }
        Spacer(Modifier.height(30.dp))
        Text("It's okay. Take a breath.", color = TextPrimary, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
        Text("Ready to come back?", color = TextSecondary, fontSize = 15.sp)
        Spacer(Modifier.height(30.dp))
        DriftButton(isDrifting = true, onClick = onResume, modifier = Modifier.widthIn(max = 280.dp))
    }
}

@Composable
private fun BreakContent(state: SessionState.OnBreak) {
    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("BREAK TIME", color = TextSecondary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Spacer(Modifier.height(30.dp))
        FocusOrb(state = FocusOrbState.BREAK, size = 260.dp) {
            Text(state.remainingMs.toMMSS(), color = TextPrimary, fontFamily = DmMono, fontSize = 56.sp)
        }
        Spacer(Modifier.height(20.dp))
        Text(
            if (state.breakType == SessionType.LONG_BREAK) "Long break — you earned it" else "Short break",
            color = TextSecondary,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun SessionScaffold(
    headerLabel: String,
    orbState: FocusOrbState,
    remainingMs: Long,
    totalMs: Long,
    task: String,
    nextLabel: String,
    isPaused: Boolean,
    onPauseResume: () -> Unit,
    onReset: () -> Unit,
    onCompleteEarly: () -> Unit,
    bottomContent: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(headerLabel, color = TextSecondary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Spacer(Modifier.height(30.dp))

            FocusOrb(state = orbState, size = 280.dp) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(remainingMs.toMMSS(), color = TextPrimary, fontFamily = DmMono, fontSize = 64.sp, fontWeight = FontWeight.Medium)
                    if (task.isNotBlank()) {
                        Text(task, color = TextPrimary.copy(alpha = 0.8f), fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }

            Spacer(Modifier.height(30.dp))
            Box(modifier = Modifier.widthIn(max = 280.dp).fillMaxWidth()) {
                Column {
                    val progress = if (totalMs > 0) 1f - (remainingMs.toFloat() / totalMs) else 0f
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(SurfaceElevated, RoundedCornerShape(99.dp))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progress.coerceIn(0f, 1f))
                                .background(IndigoPrimary, RoundedCornerShape(99.dp))
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(nextLabel, color = TextSecondary, fontSize = 13.sp, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }

            Spacer(Modifier.height(24.dp))
            SessionControlsBar(
                isPaused = isPaused,
                onPauseResumeClick = onPauseResume,
                onResetClick = onReset,
                onCompleteEarlyClick = onCompleteEarly
            )
        }

        Box(modifier = Modifier.padding(horizontal = 34.dp, vertical = 40.dp)) {
            bottomContent()
        }
    }
}
