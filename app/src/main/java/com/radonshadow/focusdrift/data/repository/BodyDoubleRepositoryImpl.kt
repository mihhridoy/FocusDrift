package com.radonshadow.focusdrift.data.repository

import com.radonshadow.focusdrift.data.remote.firebase.FirebaseBodyDoubleManager
import com.radonshadow.focusdrift.domain.model.BodyDoubleRoom
import com.radonshadow.focusdrift.domain.model.Participant
import com.radonshadow.focusdrift.domain.repository.BodyDoubleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BodyDoubleRepositoryImpl @Inject constructor(
    private val firebaseManager: FirebaseBodyDoubleManager
) : BodyDoubleRepository {

    override fun observeActiveRooms(): Flow<List<BodyDoubleRoom>> = firebaseManager.observeActiveRooms()

    override fun observeRoom(roomId: String): Flow<BodyDoubleRoom?> = firebaseManager.observeRoom(roomId)

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
