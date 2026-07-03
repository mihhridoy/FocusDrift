package com.radonshadow.focusdrift

import android.app.Application
import android.util.Log
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
import java.io.File
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.Date
import javax.inject.Inject

@HiltAndroidApp
class FocusDriftApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        installCrashLogger()
        super.onCreate()
        NotificationUtils.ensureChannels(this)
        scheduleBackgroundWork()
    }

    /**
     * Writes any uncaught exception to a plain-text file under this app's external files dir
     * (no permission needed, readable with any file manager app) so a crash can be diagnosed
     * from a real device without ADB. Remove once the app is stable in production.
     */
    private fun installCrashLogger() {
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            runCatching {
                val file = File(getExternalFilesDir(null), "last_crash.txt")
                file.writeText(
                    "Time: ${Date()}\nThread: ${thread.name}\n\n${Log.getStackTraceString(throwable)}"
                )
            }
            previousHandler?.uncaughtException(thread, throwable)
        }
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
