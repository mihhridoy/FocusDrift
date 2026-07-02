package com.radonshadow.focusdrift.ui.screens.timer

import com.radonshadow.focusdrift.domain.model.SessionState

data class TimerUiState(
    val sessionState: SessionState = SessionState.Idle,
    val task: String = "",
    val focusMinutes: Int = 25,
    val dailyGoalSessions: Int = 5,
    val nextSessionNumber: Int = 1,
    val selectedOrbSkinId: String = "orb_default"
)
