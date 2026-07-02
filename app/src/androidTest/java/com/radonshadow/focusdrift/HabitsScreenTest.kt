package com.radonshadow.focusdrift

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.radonshadow.focusdrift.domain.model.Habit
import com.radonshadow.focusdrift.domain.model.HabitFrequency
import com.radonshadow.focusdrift.domain.model.StreakInfo
import com.radonshadow.focusdrift.ui.components.HabitCard
import com.radonshadow.focusdrift.ui.theme.FocusDriftTheme
import org.junit.Rule
import org.junit.Test

/**
 * Exercises [HabitCard] directly (rather than the Hilt-backed
 * [com.radonshadow.focusdrift.ui.screens.habits.HabitsScreen]) so the test doesn't need a full DI
 * graph (Room, WorkManager) wired up under instrumentation.
 */
class HabitsScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val habit = Habit(
        id = 1L,
        userId = "test_user",
        name = "Morning water",
        emoji = "💧",
        frequency = HabitFrequency.DAILY,
        createdAt = 0L,
        colorTint = "#7C6FE0"
    )

    @Test
    fun incompleteHabit_showsMarkDoneAction_andInvokesCallbackOnTap() {
        var markedDone = false
        val streak = StreakInfo(
            habitId = 1L,
            currentStreak = 12,
            longestStreak = 12,
            isCompletedToday = false,
            isPaused = false,
            weekCompletion = listOf(true, true, true, true, true, false, false)
        )

        composeRule.setContent {
            FocusDriftTheme {
                HabitCard(habit = habit, streakInfo = streak, onMarkDone = { markedDone = true })
            }
        }

        composeRule.onNodeWithText("Mark done today").assertExists()
        composeRule.onNodeWithText("Mark done today").performClick()
        assert(markedDone)
    }

    @Test
    fun completedHabit_showsDoneState() {
        val streak = StreakInfo(
            habitId = 1L,
            currentStreak = 8,
            longestStreak = 8,
            isCompletedToday = true,
            isPaused = false,
            weekCompletion = listOf(true, true, true, true, true, true, true)
        )

        composeRule.setContent {
            FocusDriftTheme {
                HabitCard(habit = habit, streakInfo = streak, onMarkDone = {})
            }
        }

        composeRule.onNodeWithText("Done today").assertExists()
    }

    @Test
    fun pausedStreak_showsReassuringCopyInsteadOfShame() {
        val streak = StreakInfo(
            habitId = 1L,
            currentStreak = 0,
            longestStreak = 8,
            isCompletedToday = false,
            isPaused = true,
            weekCompletion = List(7) { false }
        )

        composeRule.setContent {
            FocusDriftTheme {
                HabitCard(habit = habit, streakInfo = streak, onMarkDone = {})
            }
        }

        composeRule.onNodeWithText("Streak paused — not broken. Resume today.").assertExists()
    }
}
