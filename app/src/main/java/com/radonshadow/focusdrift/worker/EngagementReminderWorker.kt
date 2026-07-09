package com.radonshadow.focusdrift.worker

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.radonshadow.focusdrift.core.utils.NotificationUtils
import com.radonshadow.focusdrift.domain.usecase.progress.GetUserProgressUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalTime

/**
 * Runs hourly and sends up to three focus check-ins a day, separate from per-habit reminders:
 * a morning kickoff, a midday nudge if no session has happened yet, and an evening warning
 * while the daily goal (and any streak riding on it) is still unmet. Each slot fires at most
 * once because the hourly cadence matches the one-hour detection window, and slots go quiet
 * as soon as the user is on track — an active user hears nothing after the morning ping.
 */
@HiltWorker
class EngagementReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val getUserProgressUseCase: GetUserProgressUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val hour = LocalTime.now().hour
        val progress = getUserProgressUseCase().first()

        val notification = when (hour) {
            MORNING_HOUR -> NotificationUtils.engagementNotification(
                applicationContext,
                title = if (progress.currentStreak > 0) "Day ${progress.currentStreak + 1} starts now" else "Ready to focus?",
                text = if (progress.currentStreak > 0) {
                    "Your ${progress.currentStreak}-day streak is counting on today's first session."
                } else {
                    "One focus session this morning sets up your whole day."
                }
            )
            MIDDAY_HOUR -> if (progress.sessionsToday == 0) {
                NotificationUtils.engagementNotification(
                    applicationContext,
                    title = "Your focus orb misses you",
                    text = "No sessions yet today. Even 10 minutes counts — start small."
                )
            } else null
            EVENING_HOUR -> if (progress.sessionsToday < progress.dailyGoalSessions) {
                val remaining = progress.dailyGoalSessions - progress.sessionsToday
                NotificationUtils.engagementNotification(
                    applicationContext,
                    title = if (progress.currentStreak > 0) "Streak at risk" else "Still time to hit your goal",
                    text = if (progress.currentStreak > 0) {
                        "Your ${progress.currentStreak}-day streak ends at midnight. $remaining more session${if (remaining == 1) "" else "s"} keeps it alive."
                    } else {
                        "$remaining session${if (remaining == 1) "" else "s"} left to hit today's goal. One evening session gets you closer."
                    }
                )
            } else null
            else -> null
        }

        notification?.let {
            NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID_BASE + hour, it)
        }
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "engagement_reminder_work"
        private const val NOTIFICATION_ID_BASE = 4000
        private const val MORNING_HOUR = 9
        private const val MIDDAY_HOUR = 13
        private const val EVENING_HOUR = 20
    }
}
