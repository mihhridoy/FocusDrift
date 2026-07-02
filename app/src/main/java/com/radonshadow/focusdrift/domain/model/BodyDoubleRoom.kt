package com.radonshadow.focusdrift.domain.model

enum class ParticipantStatus {
    FOCUSING,
    ON_BREAK,
    IDLE
}

data class Participant(
    val userId: String,
    val displayName: String,
    val avatarColor: String,
    val status: ParticipantStatus,
    val currentTask: String = "",
    val joinedAt: Long,
    val lastSeen: Long
)

data class BodyDoubleRoom(
    val id: String,
    val name: String,
    val type: String,
    val createdBy: String,
    val createdAt: Long,
    val sessionDurationMs: Long,
    val sessionStartedAt: Long,
    val maxParticipants: Int = 20,
    val participants: List<Participant> = emptyList()
) {
    val participantCount: Int get() = participants.size
    val focusingCount: Int get() = participants.count { it.status == ParticipantStatus.FOCUSING }
    val onBreakCount: Int get() = participants.count { it.status == ParticipantStatus.ON_BREAK }
    val runningForMs: Long get() = (System.currentTimeMillis() - sessionStartedAt).coerceAtLeast(0)
}
