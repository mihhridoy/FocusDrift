package com.radonshadow.focusdrift.data.mapper

import com.radonshadow.focusdrift.data.remote.dto.BodyDoubleRoomDto
import com.radonshadow.focusdrift.data.remote.dto.toDomain
import com.radonshadow.focusdrift.domain.model.BodyDoubleRoom

fun BodyDoubleRoomDto.toDomain(id: String): BodyDoubleRoom = BodyDoubleRoom(
    id = id,
    name = name,
    type = type,
    createdBy = createdBy,
    createdAt = createdAt,
    sessionDurationMs = sessionDurationMs,
    sessionStartedAt = sessionStartedAt,
    maxParticipants = maxParticipants,
    participants = participants.map { (userId, dto) -> dto.toDomain(userId) }
)
