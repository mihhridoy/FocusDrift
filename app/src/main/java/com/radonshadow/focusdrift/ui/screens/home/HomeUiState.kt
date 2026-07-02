package com.radonshadow.focusdrift.ui.screens.home

import com.radonshadow.focusdrift.domain.model.BodyDoubleRoom
import com.radonshadow.focusdrift.domain.model.Habit
import com.radonshadow.focusdrift.domain.model.UserProgress

data class HabitStreakSummary(val habit: Habit, val currentStreak: Int, val isCompletedToday: Boolean)

data class HomeUiState(
    val isLoading: Boolean = true,
    val userProgress: UserProgress = UserProgress(userId = ""),
    val habitStreaks: List<HabitStreakSummary> = emptyList(),
    val activeRooms: List<BodyDoubleRoom> = emptyList()
) {
    val activeRoomParticipantCount: Int get() = activeRooms.sumOf { it.participantCount }
    val hasActiveRoom: Boolean get() = activeRooms.isNotEmpty()
}
