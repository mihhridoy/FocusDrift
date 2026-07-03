package com.radonshadow.focusdrift.data.repository

import android.util.Log
import com.radonshadow.focusdrift.data.remote.firebase.FirebaseBodyDoubleManager
import com.radonshadow.focusdrift.domain.model.BodyDoubleRoom
import com.radonshadow.focusdrift.domain.model.Participant
import com.radonshadow.focusdrift.domain.repository.BodyDoubleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class BodyDoubleRepositoryImpl @Inject constructor(
    private val firebaseManager: FirebaseBodyDoubleManager
) : BodyDoubleRepository {

    // Realtime Database reads/writes require anonymous auth (see database.rules.json). A user
    // who hasn't triggered sign-in yet (or is offline) would otherwise crash the Home screen
    // with an uncaught DatabaseError as soon as this flow is collected — fall back to an empty
    // room list instead so a Firebase hiccup never takes down the whole app.
    override fun observeActiveRooms(): Flow<List<BodyDoubleRoom>> = firebaseManager.observeActiveRooms()
        .catch { t ->
            Log.e("BodyDoubleRepository", "observeActiveRooms failed, showing no rooms", t)
            emit(emptyList())
        }

    override fun observeRoom(roomId: String): Flow<BodyDoubleRoom?> = firebaseManager.observeRoom(roomId)
        .catch { t ->
            Log.e("BodyDoubleRepository", "observeRoom failed", t)
            emit(null)
        }

    override suspend fun createRoom(name: String, type: String, sessionDurationMs: Long, createdBy: String): String =
        firebaseManager.createRoom(name, type, sessionDurationMs, createdBy)

    override suspend fun joinRoom(roomId: String, participant: Participant) =
        firebaseManager.joinRoom(roomId, participant)

    override suspend fun leaveRoom(roomId: String, userId: String) =
        firebaseManager.leaveRoom(roomId, userId)

    override suspend fun updateStatus(roomId: String, userId: String, status: String, currentTask: String) =
        firebaseManager.updateStatus(roomId, userId, status, currentTask)

    override suspend fun sendReaction(roomId: String, userId: String, reaction: String) =
        firebaseManager.sendReaction(roomId, userId, reaction)
}
