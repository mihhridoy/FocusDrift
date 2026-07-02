package com.radonshadow.focusdrift.domain.model

data class FocusSession(
    val id: Long = 0,
    val userId: String,
    val sessionType: SessionType,
    val plannedDurationMs: Long,
    val actualDurationMs: Long,
    val task: String,
    val startedAt: Long,
    val completedAt: Long?,
    val wasAbandoned: Boolean = false,
    val xpEarned: Int = 0,
    val coinsEarned: Int = 0
)
