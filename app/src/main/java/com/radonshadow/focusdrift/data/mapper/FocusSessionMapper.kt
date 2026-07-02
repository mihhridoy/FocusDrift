package com.radonshadow.focusdrift.data.mapper

import com.radonshadow.focusdrift.data.local.entity.FocusSessionEntity
import com.radonshadow.focusdrift.domain.model.FocusSession
import com.radonshadow.focusdrift.domain.model.SessionType

fun FocusSessionEntity.toDomain(): FocusSession = FocusSession(
    id = id,
    userId = userId,
    sessionType = SessionType.valueOf(sessionType),
    plannedDurationMs = plannedDurationMs,
    actualDurationMs = actualDurationMs,
    task = task,
    startedAt = startedAt,
    completedAt = completedAt,
    wasAbandoned = wasAbandoned,
    xpEarned = xpEarned,
    coinsEarned = coinsEarned
)

fun FocusSession.toEntity(): FocusSessionEntity = FocusSessionEntity(
    id = id,
    userId = userId,
    sessionType = sessionType.name,
    plannedDurationMs = plannedDurationMs,
    actualDurationMs = actualDurationMs,
    task = task,
    startedAt = startedAt,
    completedAt = completedAt,
    wasAbandoned = wasAbandoned,
    xpEarned = xpEarned,
    coinsEarned = coinsEarned
)
