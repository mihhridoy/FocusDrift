package com.radonshadow.focusdrift.data.remote.dto

data class BodyDoubleRoomDto(
    val name: String = "",
    val type: String = "",
    val createdBy: String = "",
    val createdAt: Long = 0,
    val sessionDurationMs: Long = 0,
    val sessionStartedAt: Long = 0,
    val maxParticipants: Int = 20,
    val participants: Map<String, ParticipantDto> = emptyMap()
)
