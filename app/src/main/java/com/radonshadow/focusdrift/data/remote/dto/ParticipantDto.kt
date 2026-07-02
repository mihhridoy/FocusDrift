package com.radonshadow.focusdrift.data.remote.dto

import com.radonshadow.focusdrift.domain.model.Participant
import com.radonshadow.focusdrift.domain.model.ParticipantStatus

data class ParticipantDto(
    val displayName: String = "",
    val avatarColor: String = "",
    val status: String = ParticipantStatus.FOCUSING.name,
    val currentTask: String = "",
    val joinedAt: Long = 0,
    val lastSeen: Long = 0
)

fun ParticipantDto.toDomain(userId: String): Participant = Participant(
    userId = userId,
    displayName = displayName,
    avatarColor = avatarColor,
    status = runCatching { ParticipantStatus.valueOf(status) }.getOrDefault(ParticipantStatus.FOCUSING),
    currentTask = currentTask,
    joinedAt = joinedAt,
    lastSeen = lastSeen
)

fun Participant.toDto(): ParticipantDto = ParticipantDto(
    displayName = displayName,
    avatarColor = avatarColor,
    status = status.name,
    currentTask = currentTask,
    joinedAt = joinedAt,
    lastSeen = lastSeen
)
