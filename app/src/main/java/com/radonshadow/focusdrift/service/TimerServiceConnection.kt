package com.radonshadow.focusdrift.service

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.radonshadow.focusdrift.domain.model.SessionState
import com.radonshadow.focusdrift.domain.model.SessionType
import com.radonshadow.focusdrift.domain.repository.FocusTimerController
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/** Binds to [FocusTimerService] and adapts it to the domain-facing [FocusTimerController] contract. */
@Singleton
class TimerServiceConnection @Inject constructor(
    @ApplicationContext private val context: Context
) : FocusTimerController {

    private val connectionScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var forwardingJob: Job? = null
    private var boundService: FocusTimerService? = null

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Idle)
    override val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val service = (binder as? FocusTimerService.LocalBinder)?.getService() ?: return
            boundService = service
            forwardingJob?.cancel()
            forwardingJob = connectionScope.launch {
                service.sessionState.collect { _sessionState.value = it }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            boundService = null
            forwardingJob?.cancel()
        }
    }

    private var isBound = false

    private fun ensureBound() {
        if (isBound) return
        isBound = true
        context.bindService(Intent(context, FocusTimerService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun sendAction(action: String, configure: Intent.() -> Unit = {}) {
        val intent = Intent(context, FocusTimerService::class.java).apply {
            this.action = action
            configure()
        }
        ContextCompat.startForegroundService(context, intent)
    }

    override fun start(durationMs: Long, sessionType: SessionType, task: String, sessionNumber: Int, dailyGoalSessions: Int) {
        ensureBound()
        sendAction(FocusTimerService.ACTION_START) {
            putExtra(FocusTimerService.EXTRA_DURATION_MS, durationMs)
            putExtra(FocusTimerService.EXTRA_SESSION_TYPE, sessionType.name)
            putExtra(FocusTimerService.EXTRA_TASK, task)
            putExtra(FocusTimerService.EXTRA_SESSION_NUMBER, sessionNumber)
            putExtra(FocusTimerService.EXTRA_DAILY_GOAL, dailyGoalSessions)
        }
    }

    override fun pause() {
        sendAction(FocusTimerService.ACTION_PAUSE)
    }

    override fun resume() {
        sendAction(FocusTimerService.ACTION_RESUME)
    }

    override fun markDrifting() {
        sendAction(FocusTimerService.ACTION_DRIFT)
    }

    override fun resumeFromDrift() {
        sendAction(FocusTimerService.ACTION_RESUME_FROM_DRIFT)
    }

    override fun completeEarly() {
        sendAction(FocusTimerService.ACTION_COMPLETE_EARLY)
    }

    override fun abandon() {
        sendAction(FocusTimerService.ACTION_STOP)
        releaseBinding()
    }

    private fun releaseBinding() {
        if (!isBound) return
        runCatching { context.unbindService(serviceConnection) }
        isBound = false
        boundService = null
        forwardingJob?.cancel()
    }
}
