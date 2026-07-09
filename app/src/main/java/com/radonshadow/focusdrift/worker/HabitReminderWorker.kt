package com.radonshadow.focusdrift.worker

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.radonshadow.focusdrift.core.constants.AppConstants
import com.radonshadow.focusdrift.core.utils.NotificationUtils
import com.radonshadow.focusdrift.domain.repository.HabitRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalTime

/**
 * Runs hourly; reminds about each unfinished habit up to three times a day — at its reminder
 * time, then 3 and 6 hours later — with escalating copy per round. Completing the habit stops
 * the remaining rounds, and each round fires at most once because the worker's hourly cadence
 * matches the one-hour detection window.
 */
@HiltWorker
class HabitReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val habitRepository: HabitRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val habits = habitRepository.observeHabits(AppConstants.DEFAULT_USER_ID).first()
        val now = LocalTime.now()

        habits.filter { it.reminderEnabled }.forEach { habit ->
            val reminderTime = LocalTime.of(habit.reminderHour, habit.reminderMinute)
            val round = REMINDER_ROUND_OFFSET_HOURS.indexOfFirst { offsetHours ->
                val windowStart = reminderTime.plusHours(offsetHours)
                // plusHours wraps past midnight; skip wrapped rounds so a 22:00 reminder
                // doesn't fire its follow-ups at 1am/4am.
                windowStart >= reminderTime && !now.isBefore(windowStart) && now.isBefore(windowStart.plusHours(1))
            }
            if (round >= 0 && !habitRepository.isCompletedToday(habit.id)) {
                NotificationManagerCompat.from(applicationContext).notify(
                    NOTIFICATION_ID_BASE + habit.id.toInt(),
                    NotificationUtils.habitReminderNotification(applicationContext, habit.name, round)
                )
            }
        }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "habit_reminder_work"
        private const val NOTIFICATION_ID_BASE = 2000
        private val REMINDER_ROUND_OFFSET_HOURS = listOf(0L, 3L, 6L)
    }
}
