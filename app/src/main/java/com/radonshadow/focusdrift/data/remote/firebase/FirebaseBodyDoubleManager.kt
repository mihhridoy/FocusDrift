package com.radonshadow.focusdrift.data.remote.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.radonshadow.focusdrift.core.constants.AppConstants
import com.radonshadow.focusdrift.data.mapper.toDomain
import com.radonshadow.focusdrift.data.remote.dto.BodyDoubleRoomDto
import com.radonshadow.focusdrift.data.remote.dto.toDto
import com.radonshadow.focusdrift.domain.model.BodyDoubleRoom
import com.radonshadow.focusdrift.domain.model.Participant
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class FirebaseBodyDoubleManager @Inject constructor(
    private val database: DatabaseReference
) {
    private val roomsRef get() = database.child("rooms")

    fun observeActiveRooms(): Flow<List<BodyDoubleRoom>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val staleBefore = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(AppConstants.ROOM_PARTICIPANT_STALE_MINUTES)
                val rooms = snapshot.children.mapNotNull { child ->
                    val dto = child.getValue(BodyDoubleRoomDto::class.java) ?: return@mapNotNull null
                    val id = child.key ?: return@mapNotNull null
                    dto.toDomain(id)
                }.map { room ->
                    room.copy(participants = room.participants.filter { it.lastSeen >= staleBefore })
                }.filter { it.participantCount > 0 }
                    .sortedByDescending { it.participantCount }
                trySend(rooms)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        roomsRef.addValueEventListener(listener)
        awaitClose { roomsRef.removeEventListener(listener) }
    }

    fun observeRoom(roomId: String): Flow<BodyDoubleRoom?> = callbackFlow {
        val ref = roomsRef.child(roomId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dto = snapshot.getValue(BodyDoubleRoomDto::class.java)
                trySend(dto?.toDomain(roomId))
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    suspend fun createRoom(name: String, type: String, sessionDurationMs: Long, createdBy: String): String {
        val ref = roomsRef.push()
        val now = System.currentTimeMillis()
        ref.setValue(
            BodyDoubleRoomDto(
                name = name,
                type = type,
                createdBy = createdBy,
                createdAt = now,
                sessionDurationMs = sessionDurationMs,
                sessionStartedAt = now
            )
        ).await()
        return ref.key ?: error("Firebase did not return a room id")
    }

    suspend fun joinRoom(roomId: String, participant: Participant) {
        roomsRef.child(roomId).child("participants").child(participant.userId)
            .setValue(participant.toDto())
            .await()
    }

    suspend fun leaveRoom(roomId: String, userId: String) {
        roomsRef.child(roomId).child("participants").child(userId).removeValue().await()
    }

    suspend fun updateStatus(roomId: String, userId: String, status: String, currentTask: String) {
        roomsRef.child(roomId).child("participants").child(userId).updateChildren(
            mapOf(
                "status" to status,
                "currentTask" to currentTask,
                "lastSeen" to System.currentTimeMillis()
            )
        ).await()
    }

    suspend fun sendReaction(roomId: String, userId: String, reaction: String) {
        roomsRef.child(roomId).child("reactions").push().setValue(
            mapOf("userId" to userId, "reaction" to reaction, "sentAt" to System.currentTimeMillis())
        ).await()
    }
}
