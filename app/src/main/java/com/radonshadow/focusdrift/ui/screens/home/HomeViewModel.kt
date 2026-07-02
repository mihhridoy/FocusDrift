package com.radonshadow.focusdrift.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radonshadow.focusdrift.core.extensions.stateInViewModel
import com.radonshadow.focusdrift.domain.model.Habit
import com.radonshadow.focusdrift.domain.model.StreakInfo
import com.radonshadow.focusdrift.domain.usecase.bodyDouble.GetActiveRoomsUseCase
import com.radonshadow.focusdrift.domain.usecase.habit.GetHabitStreakUseCase
import com.radonshadow.focusdrift.domain.usecase.habit.GetTodayHabitsUseCase
import com.radonshadow.focusdrift.domain.usecase.progress.GetUserProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    getUserProgressUseCase: GetUserProgressUseCase,
    getTodayHabitsUseCase: GetTodayHabitsUseCase,
    getActiveRoomsUseCase: GetActiveRoomsUseCase,
    private val getHabitStreakUseCase: GetHabitStreakUseCase
) : ViewModel() {

    private val habitStreaksFlow = getTodayHabitsUseCase().flatMapLatest { habits ->
        if (habits.isEmpty()) {
            flowOf(emptyList())
        } else {
            combine(habits.map { habit -> streakSummaryFlow(habit) }) { it.toList() }
        }
    }

    private fun streakSummaryFlow(habit: Habit) = getHabitStreakUseCase(habit.id).map { streak: StreakInfo ->
        HabitStreakSummary(habit, streak.currentStreak, streak.isCompletedToday)
    }

    val uiState = combine(
        getUserProgressUseCase(),
        habitStreaksFlow,
        getActiveRoomsUseCase()
    ) { progress, habitStreaks, rooms ->
        HomeUiState(isLoading = false, userProgress = progress, habitStreaks = habitStreaks.take(3), activeRooms = rooms)
    }.stateInViewModel(viewModelScope, HomeUiState())
}
