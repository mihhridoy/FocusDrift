package com.radonshadow.focusdrift

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.radonshadow.focusdrift.core.constants.AppConstants
import com.radonshadow.focusdrift.core.utils.NotificationUtils
import com.radonshadow.focusdrift.worker.DailyResetWorker
import com.radonshadow.focusdrift.worker.HabitReminderWorker
import com.radonshadow.focusdrift.worker.StreakProtectionWorker
import dagger.hilt.android.HiltAndroidApp
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltAndroidApp
class FocusDriftApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()
        NotificationUtils.ensureChannels(this)
        scheduleBackgroundWork()
    }

    private fun scheduleBackgroundWork() {
        val workManager = WorkManager.getInstance(this)

        workManager.enqueueUniquePeriodicWork(
            HabitReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<HabitReminderWorker>(Duration.ofHours(1)).build()
        )

        workManager.enqueueUniquePeriodicWork(
            StreakProtectionWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<StreakProtectionWorker>(Duration.ofDays(1))
                .setInitialDelay(delayUntil(AppConstants.STREAK_PROTECTION_HOUR, 0))
                .build()
        )

        workManager.enqueueUniquePeriodicWork(
            DailyResetWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            PeriodicWorkRequestBuilder<DailyResetWorker>(Duration.ofDays(1))
                .setInitialDelay(delayUntil(0, 5))
                .build()
        )
    }

    private fun delayUntil(hour: Int, minute: Int): Duration {
        val now = LocalDateTime.now()
        var target = now.toLocalDate().atTime(LocalTime.of(hour, minute))
        if (target.isBefore(now)) target = target.plusDays(1)
        return Duration.of(ChronoUnit.MINUTES.between(now, target), ChronoUnit.MINUTES)
    }
}
