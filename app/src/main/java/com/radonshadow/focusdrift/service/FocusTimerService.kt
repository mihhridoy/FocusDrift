package com.radonshadow.focusdrift.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.radonshadow.focusdrift.core.constants.AppConstants
import com.radonshadow.focusdrift.core.constants.TimerConstants
import com.radonshadow.focusdrift.core.utils.HapticUtils
import com.radonshadow.focusdrift.core.utils.NotificationUtils
import com.radonshadow.focusdrift.core.utils.SoundUtils
import com.radonshadow.focusdrift.domain.model.SessionState
import com.radonshadow.focusdrift.domain.model.SessionType
import com.radonshadow.focusdrift.domain.usecase.timer.CompleteSessionUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Owns the focus/break countdown as a foreground service. A ViewModel-scoped coroutine would be
 * killed by the OS once the app backgrounds, which is unacceptable for a timer ADHD users rely on
 * to keep running while they context-switch away from the app.
 */
@AndroidEntryPoint
class FocusTimerService : Service() {

    @Inject lateinit var completeSessionUseCase: CompleteSessionUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val binder = LocalBinder()

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Idle)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    private var countDownTimer: CountDownTimer? = null
    private var remainingMs = 0L
    private var totalMs = 0L
    private var sessionType = SessionType.FOCUS
    private var currentTask = ""
    private var sessionNumber = 1
    private var dailyGoalSessions = TimerConstants.DEFAULT_DAILY_SESSION_GOAL

    inner class LocalBinder : Binder() {
        fun getService(): FocusTimerService = this@FocusTimerService
    }

    override fun onCreate() {
        super.onCreate()
        NotificationUtils.ensureChannels(this)
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTimer(
                durationMs = intent.getLongExtra(EXTRA_DURATION_MS, TimerConstants.DEFAULT_FOCUS_MINUTES * 60_000L),
                type = SessionType.valueOf(intent.getStringExtra(EXTRA_SESSION_TYPE) ?: SessionType.FOCUS.name),
                task = intent.getStringExtra(EXTRA_TASK) ?: "",
                number = intent.getIntExtra(EXTRA_SESSION_NUMBER, 1),
                dailyGoal = intent.getIntExtra(EXTRA_DAILY_GOAL, TimerConstants.DEFAULT_DAILY_SESSION_GOAL)
            )
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
            ACTION_DRIFT -> onUserDrifting()
            ACTION_RESUME_FROM_DRIFT -> resumeFromDrift()
            ACTION_COMPLETE_EARLY -> completeNow()
            ACTION_STOP -> stopTimerAndService()
        }
        return START_STICKY
    }

    fun startTimer(durationMs: Long, type: SessionType, task: String, number: Int, dailyGoal: Int) {
        sessionType = type
        remainingMs = durationMs
        totalMs = durationMs
        currentTask = task
        sessionNumber = number
        dailyGoalSessions = dailyGoal
        startForeground(NOTIFICATION_ID, NotificationUtils.timerNotification(this, type.label, remainingMs))
        tick()
    }

    fun pauseTimer() {
        countDownTimer?.cancel()
        _sessionState.value = SessionState.Paused(remainingMs, totalMs, sessionType, currentTask, sessionNumber)
    }

    fun resumeTimer() {
        tick()
    }

    fun onUserDrifting() {
        countDownTimer?.cancel()
        HapticUtils.drift(this)
        _sessionState.value = SessionState.Drifting(remainingMs, totalMs, sessionType, sessionNumber)
    }

    fun resumeFromDrift() {
        tick()
    }

    fun completeNow() {
        countDownTimer?.cancel()
        onSessionComplete()
    }

    fun abandon() {
        countDownTimer?.cancel()
        _sessionState.value = SessionState.Idle
        stopTimerAndService()
    }

    private fun tick() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(remainingMs, TimerConstants.TICK_INTERVAL_MS) {
            override fun onTick(remaining: Long) {
                remainingMs = remaining
                _sessionState.value = if (sessionType == SessionType.FOCUS) {
                    SessionState.Running(remainingMs, totalMs, sessionType, currentTask, sessionNumber, dailyGoalSessions)
                } else {
                    SessionState.OnBreak(remainingMs, totalMs, sessionType)
                }
                NotificationManagerCompat.from(this@FocusTimerService).notify(
                    NOTIFICATION_ID,
                    NotificationUtils.timerNotification(this@FocusTimerService, sessionType.label, remainingMs)
                )
            }

            override fun onFinish() {
                remainingMs = 0
                onSessionComplete()
            }
        }.start()
    }

    private fun onSessionComplete() {
        val actualDurationMs = totalMs - remainingMs
        SoundUtils.playSessionComplete(this)
        HapticUtils.sessionComplete(this)

        serviceScope.launch {
            val result = completeSessionUseCase(
                sessionType = sessionType,
                plannedDurationMs = totalMs,
                actualDurationMs = actualDurationMs.coerceAtLeast(0),
                task = currentTask,
                userId = AppConstants.DEFAULT_USER_ID
            )
            _sessionState.value = SessionState.Complete(
                sessionType = sessionType,
                focusDurationMs = actualDurationMs,
                xpEarned = result.xpEarned,
                coinsEarned = result.coinsEarned,
                xpBefore = result.xpBefore,
                xpAfter = result.xpAfter,
                currentStreak = result.currentStreak
            )
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

    private fun stopTimerAndService() {
        countDownTimer?.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        countDownTimer?.cancel()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START = "com.radonshadow.focusdrift.action.START"
        const val ACTION_PAUSE = "com.radonshadow.focusdrift.action.PAUSE"
        const val ACTION_RESUME = "com.radonshadow.focusdrift.action.RESUME"
        const val ACTION_DRIFT = "com.radonshadow.focusdrift.action.DRIFT"
        const val ACTION_RESUME_FROM_DRIFT = "com.radonshadow.focusdrift.action.RESUME_FROM_DRIFT"
        const val ACTION_COMPLETE_EARLY = "com.radonshadow.focusdrift.action.COMPLETE_EARLY"
        const val ACTION_STOP = "com.radonshadow.focusdrift.action.STOP"

        const val EXTRA_DURATION_MS = "EXTRA_DURATION_MS"
        const val EXTRA_SESSION_TYPE = "EXTRA_SESSION_TYPE"
        const val EXTRA_TASK = "EXTRA_TASK"
        const val EXTRA_SESSION_NUMBER = "EXTRA_SESSION_NUMBER"
        const val EXTRA_DAILY_GOAL = "EXTRA_DAILY_GOAL"

        const val NOTIFICATION_ID = 1001
    }
}
