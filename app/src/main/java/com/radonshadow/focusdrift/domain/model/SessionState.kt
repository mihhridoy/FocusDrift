package com.radonshadow.focusdrift.domain.model

sealed class SessionState {
    object Idle : SessionState()

    data class Running(
        val remainingMs: Long,
        val totalMs: Long,
        val sessionType: SessionType,
        val currentTask: String = "",
        val sessionNumber: Int = 1,
        val dailyGoalSessions: Int = 5
    ) : SessionState()

    data class Paused(
        val remainingMs: Long,
        val totalMs: Long,
        val sessionType: SessionType,
        val currentTask: String = "",
        val sessionNumber: Int = 1
    ) : SessionState()

    data class Drifting(
        val remainingMs: Long,
        val totalMs: Long,
        val sessionType: SessionType,
        val sessionNumber: Int = 1
    ) : SessionState()

    data class OnBreak(
        val remainingMs: Long,
        val totalMs: Long,
        val breakType: SessionType
    ) : SessionState()

    data class Complete(
        val sessionType: SessionType,
        val focusDurationMs: Long,
        val xpEarned: Int,
        val coinsEarned: Int,
        val xpBefore: Int,
        val xpAfter: Int,
        val currentStreak: Int
    ) : SessionState()
}
