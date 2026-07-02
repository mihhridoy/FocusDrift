package com.radonshadow.focusdrift.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val userId: String,
    val totalXp: Int = 0,
    val coins: Int = 0,
    val totalSessions: Int = 0,
    val totalFocusMinutes: Int = 0,
    val sessionsToday: Int = 0,
    val dailyGoalSessions: Int = 5,
    val xpEarnedToday: Int = 0,
    val focusMinutesToday: Int = 0,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastActiveDate: String = ""
)
