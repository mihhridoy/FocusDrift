package com.radonshadow.focusdrift.domain.repository

import com.radonshadow.focusdrift.domain.model.BodyDoubleRoom
import com.radonshadow.focusdrift.domain.model.Participant
import kotlinx.coroutines.flow.Flow

interface BodyDoubleRepository {
    fun observeActiveRooms(): Flow<List<BodyDoubleRoom>>
    fun observeRoom(roomId: String): Flow<BodyDoubleRoom?>
    suspend fun createRoom(name: String, type: String, sessionDurationMs: Long, createdBy: String): String
    suspend fun joinRoom(roomId: String, participant: Participant)
    suspend fun leaveRoom(roomId: String, userId: String)
    suspend fun updateStatus(roomId: String, userId: String, status: String, currentTask: String)
    suspend fun sendReaction(roomId: String, userId: String, reaction: String)
}
