package com.radonshadow.focusdrift.domain.model

data class Habit(
    val id: Long = 0,
    val userId: String,
    val name: String,
    val emoji: String,
    val frequency: HabitFrequency,
    val customDays: Set<Int> = emptySet(), // 1=Mon .. 7=Sun, only used when frequency == CUSTOM
    val reminderEnabled: Boolean = false,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
    val createdAt: Long,
    val colorTint: String
)

data class HabitCompletion(
    val id: Long = 0,
    val habitId: Long,
    val completedDate: String, // ISO local date, yyyy-MM-dd
    val completedAt: Long
)

data class StreakInfo(
    val habitId: Long,
    val currentStreak: Int,
    val longestStreak: Int,
    val isCompletedToday: Boolean,
    val isPaused: Boolean,
    val weekCompletion: List<Boolean> // Mon..Sun, size 7
)
