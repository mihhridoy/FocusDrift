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

/**
 * Scheduled for [AppConstants.STREAK_PROTECTION_HOUR] daily. Independent of each habit's personal
 * reminder setting — this is the safety net that warns before a live streak resets at midnight.
 */
@HiltWorker
class StreakProtectionWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val habitRepository: HabitRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val habits = habitRepository.observeHabits(AppConstants.DEFAULT_USER_ID).first()

        habits.forEach { habit ->
            val streak = habitRepository.getStreakInfo(habit.id)
            if (streak.currentStreak > 0 && !streak.isCompletedToday) {
                NotificationManagerCompat.from(applicationContext).notify(
                    NOTIFICATION_ID_BASE + habit.id.toInt(),
                    NotificationUtils.habitReminderNotification(applicationContext, habit.name)
                )
            }
        }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "streak_protection_work"
        private const val NOTIFICATION_ID_BASE = 3000
    }
}
