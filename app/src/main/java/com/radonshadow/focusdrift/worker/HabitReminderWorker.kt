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

/** Runs hourly; fires a reminder for each habit whose personal reminder time has just passed and isn't done yet. */
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
            val withinWindow = !now.isBefore(reminderTime) && now.isBefore(reminderTime.plusHours(1))
            if (withinWindow && !habitRepository.isCompletedToday(habit.id)) {
                NotificationManagerCompat.from(applicationContext).notify(
                    NOTIFICATION_ID_BASE + habit.id.toInt(),
                    NotificationUtils.habitReminderNotification(applicationContext, habit.name)
                )
            }
        }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "habit_reminder_work"
        private const val NOTIFICATION_ID_BASE = 2000
    }
}
