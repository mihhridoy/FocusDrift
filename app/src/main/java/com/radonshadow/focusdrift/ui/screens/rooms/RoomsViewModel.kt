package com.radonshadow.focusdrift.ui.screens.rooms

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radonshadow.focusdrift.core.constants.SubscriptionConstants
import com.radonshadow.focusdrift.core.extensions.stateInViewModel
import com.radonshadow.focusdrift.data.local.preferences.UserPreferences
import com.radonshadow.focusdrift.data.remote.firebase.FirebaseAuthManager
import com.radonshadow.focusdrift.domain.model.BodyDoubleRoom
import com.radonshadow.focusdrift.domain.repository.BodyDoubleRepository
import com.radonshadow.focusdrift.domain.repository.SubscriptionRepository
import com.radonshadow.focusdrift.domain.usecase.bodyDouble.CreateRoomUseCase
import com.radonshadow.focusdrift.domain.usecase.bodyDouble.GetActiveRoomsUseCase
import com.radonshadow.focusdrift.domain.usecase.bodyDouble.JoinRoomUseCase
import com.radonshadow.focusdrift.domain.usecase.bodyDouble.LeaveRoomUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Body-doubling identity is the Firebase anonymous auth uid, not the local Room [AppConstants]
 * user id — rooms are shared across devices/installs, so every participant needs a uid that's
 * actually unique instead of the fixed local single-user id used for on-device data.
 */
@HiltViewModel
class RoomsViewModel @Inject constructor(
    getActiveRoomsUseCase: GetActiveRoomsUseCase,
    private val joinRoomUseCase: JoinRoomUseCase,
    private val leaveRoomUseCase: LeaveRoomUseCase,
    private val createRoomUseCase: CreateRoomUseCase,
    private val bodyDoubleRepository: BodyDoubleRepository,
    private val userPreferences: UserPreferences,
    private val firebaseAuthManager: FirebaseAuthManager,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    val activeRooms: StateFlow<List<BodyDoubleRoom>> =
        getActiveRoomsUseCase().stateInViewModel(viewModelScope, emptyList())

    val isPro: StateFlow<Boolean> = subscriptionRepository.observeSubscriptionStatus()
        .map { it.isPro }
        .stateInViewModel(viewModelScope, false)

    private val _currentRoom = MutableStateFlow<BodyDoubleRoom?>(null)
    val currentRoom: StateFlow<BodyDoubleRoom?> = _currentRoom.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isBusy = MutableStateFlow(false)
    val isBusy: StateFlow<Boolean> = _isBusy.asStateFlow()

    fun clearError() {
        _errorMessage.value = null
    }

    fun observeRoom(roomId: String) {
        viewModelScope.launch {
            bodyDoubleRepository.observeRoom(roomId).collect { _currentRoom.value = it }
        }
    }

    /**
     * Every room action below does a real network round-trip (Firebase anonymous auth, then a
     * realtime-db write) that has no offline queuing the way plain db writes do — a flaky or
     * absent connection throws straight out of await(), so every call site is wrapped here
     * instead of crashing the app on a spotty connection.
     */
    private fun launchGuarded(block: suspend () -> Unit) {
        viewModelScope.launch {
            _isBusy.value = true
            try {
                block()
            } catch (t: Throwable) {
                Log.e("RoomsViewModel", "Room action failed", t)
                _errorMessage.value = "Connection issue — check your internet and try again."
            } finally {
                _isBusy.value = false
            }
        }
    }

    /** Body doubling is a Pro-only feature (see [SubscriptionConstants.FREE_BODY_DOUBLE_ROOMS]). */
    private fun requiresPro(): Boolean {
        if (SubscriptionConstants.FREE_BODY_DOUBLE_ROOMS || isPro.value) return false
        _errorMessage.value = "Body doubling rooms are a Pro feature. Upgrade to Pro to join or host a room."
        return true
    }

    fun joinRoom(roomId: String, currentTask: String, onJoined: () -> Unit) {
        if (requiresPro()) return
        launchGuarded {
            val userId = firebaseAuthManager.ensureSignedIn()
            val name = userPreferences.displayName.first()
            val color = userPreferences.avatarColor.first()
            joinRoomUseCase(roomId, name, color, currentTask, userId)
            onJoined()
        }
    }

    fun leaveRoom(roomId: String) {
        launchGuarded { leaveRoomUseCase(roomId, firebaseAuthManager.ensureSignedIn()) }
    }

    fun sendReaction(roomId: String, reaction: String) {
        if (requiresPro()) return
        launchGuarded {
            bodyDoubleRepository.sendReaction(roomId, firebaseAuthManager.ensureSignedIn(), reaction)
        }
    }

    fun createRoom(name: String, type: String, sessionDurationMs: Long, onCreated: (String) -> Unit) {
        if (requiresPro()) return
        launchGuarded {
            val userId = firebaseAuthManager.ensureSignedIn()
            val roomId = createRoomUseCase(name, type, sessionDurationMs, userId)
            joinRoomUseCase(roomId, userPreferences.displayName.first(), userPreferences.avatarColor.first(), "", userId)
            onCreated(roomId)
        }
    }
}
