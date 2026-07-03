package com.radonshadow.focusdrift.ui.screens.habits

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radonshadow.focusdrift.core.constants.SubscriptionConstants
import com.radonshadow.focusdrift.core.extensions.stateInViewModel
import com.radonshadow.focusdrift.domain.model.Habit
import com.radonshadow.focusdrift.domain.model.HabitFrequency
import com.radonshadow.focusdrift.domain.model.StreakInfo
import com.radonshadow.focusdrift.domain.repository.SubscriptionRepository
import com.radonshadow.focusdrift.domain.usecase.habit.CompleteHabitTodayUseCase
import com.radonshadow.focusdrift.domain.usecase.habit.CreateHabitUseCase
import com.radonshadow.focusdrift.domain.usecase.habit.DeleteHabitUseCase
import com.radonshadow.focusdrift.domain.usecase.habit.GetHabitStreakUseCase
import com.radonshadow.focusdrift.domain.usecase.habit.GetTodayHabitsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HabitWithStreak(val habit: Habit, val streak: StreakInfo)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HabitsViewModel @Inject constructor(
    getTodayHabitsUseCase: GetTodayHabitsUseCase,
    private val getHabitStreakUseCase: GetHabitStreakUseCase,
    private val createHabitUseCase: CreateHabitUseCase,
    private val completeHabitTodayUseCase: CompleteHabitTodayUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    val habitsWithStreaks = getTodayHabitsUseCase().flatMapLatest { habits ->
        if (habits.isEmpty()) {
            flowOf(emptyList())
        } else {
            combine(habits.map { habit -> getHabitStreakUseCase(habit.id).map { HabitWithStreak(habit, it) } }) { it.toList() }
        }
    }.stateInViewModel(viewModelScope, emptyList())

    val isPro: StateFlow<Boolean> = subscriptionRepository.observeSubscriptionStatus()
        .map { it.isPro }
        .stateInViewModel(viewModelScope, false)

    val habitCapReached: StateFlow<Boolean> = combine(habitsWithStreaks, isPro) { habits, pro ->
        !pro && habits.size >= SubscriptionConstants.FREE_HABITS_MAX
    }.stateInViewModel(viewModelScope, false)

    private val _upgradeMessage = MutableStateFlow<String?>(null)
    val upgradeMessage: StateFlow<String?> = _upgradeMessage.asStateFlow()

    fun dismissUpgradeMessage() {
        _upgradeMessage.value = null
    }

    fun markDone(habitId: Long) {
        viewModelScope.launch { completeHabitTodayUseCase(habitId) }
    }

    fun deleteHabit(habitId: Long) {
        viewModelScope.launch { deleteHabitUseCase(habitId) }
    }

    fun createHabit(name: String, emoji: String, frequency: HabitFrequency, colorTint: String, onDone: () -> Unit) {
        viewModelScope.launch {
            // Read the cap directly instead of trusting habitCapReached.value here — that
            // StateFlow only reflects reality once the UI is actively collecting it, and this
            // is the actual gate, not just the display hint.
            val isPro = subscriptionRepository.getSubscriptionStatus().isPro
            if (!isPro && habitsWithStreaks.value.size >= SubscriptionConstants.FREE_HABITS_MAX) {
                _upgradeMessage.value =
                    "Free plan is limited to ${SubscriptionConstants.FREE_HABITS_MAX} habits. Upgrade to Pro for unlimited habits."
                return@launch
            }
            createHabitUseCase(name = name, emoji = emoji, frequency = frequency, colorTint = colorTint)
            onDone()
        }
    }
}
