package com.radonshadow.focusdrift.domain.model

data class UserProgress(
    val userId: String,
    val level: Int = 1,
    val totalXp: Int = 0,
    val xpIntoLevel: Int = 0,
    val xpToNextLevel: Int = 100,
    val coins: Int = 0,
    val totalSessions: Int = 0,
    val totalFocusMinutes: Int = 0,
    val sessionsToday: Int = 0,
    val dailyGoalSessions: Int = 5,
    val xpEarnedToday: Int = 0,
    val focusMinutesToday: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0
)

data class XpReward(
    val xpEarned: Int,
    val coinsEarned: Int,
    val xpBefore: Int,
    val xpAfter: Int,
    val levelBefore: Int,
    val levelAfter: Int,
    val leveledUp: Boolean
)
