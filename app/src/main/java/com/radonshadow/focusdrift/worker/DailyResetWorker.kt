package com.radonshadow.focusdrift.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.radonshadow.focusdrift.core.constants.AppConstants
import com.radonshadow.focusdrift.domain.repository.UserProgressRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Scheduled around local midnight. [UserProgressRepositoryImpl] also lazily resets daily counters
 * the next time it's read, so this worker exists to make the reset happen promptly even if the
 * app stays closed overnight (e.g. before a morning reminder notification is shown).
 */
@HiltWorker
class DailyResetWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val userProgressRepository: UserProgressRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        userProgressRepository.resetDailyCounters(AppConstants.DEFAULT_USER_ID)
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "daily_reset_work"
    }
}
