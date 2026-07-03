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
import com.radonshadow.focusdrift.data.remote.firebase.FirebaseAuthManager
import com.radonshadow.focusdrift.worker.DailyResetWorker
import com.radonshadow.focusdrift.worker.HabitReminderWorker
import com.radonshadow.focusdrift.worker.StreakProtectionWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    @Inject lateinit var firebaseAuthManager: FirebaseAuthManager

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        installCrashLogger()
        super.onCreate()
        NotificationUtils.ensureChannels(this)
        scheduleBackgroundWork()
        signInAnonymouslyForBodyDoubling()
    }

    /**
     * Body doubling rooms are gated by `auth != null` in database.rules.json. Sign in as soon
     * as the process starts so the Home screen's room list is already authorized by the time
     * the user reaches it, instead of racing (or failing outright) on first launch.
     */
    private fun signInAnonymouslyForBodyDoubling() {
        CoroutineScope(Dispatchers.IO).launch {
            runCatching { firebaseAuthManager.ensureSignedIn() }
                .onFailure { Log.e("FocusDriftApp", "Anonymous sign-in failed", it) }
        }
    }

    /**
     * Writes any uncaught exception to a plain-text file under this app's external files dir
     * (no permission needed, readable with any file manager app) so a crash can be diagnosed
     * from a real device without ADB. Remove once the app is stable in production.
     */
    private fun installCrashLogger() {
        val previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val report = "Time: ${Date()}\nThread: ${thread.name}\n\n${Log.getStackTraceString(throwable)}"
            Log.e("FocusDriftCrash", report)
            // filesDir is always available regardless of external storage state, unlike
            // getExternalFilesDir() which can return null and silently drop the write.
            runCatching { File(filesDir, "last_crash.txt").writeText(report) }
            runCatching { getExternalFilesDir(null)?.let { File(it, "last_crash.txt").writeText(report) } }
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
