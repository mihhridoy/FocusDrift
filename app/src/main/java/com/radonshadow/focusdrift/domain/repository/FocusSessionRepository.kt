package com.radonshadow.focusdrift.domain.repository

import com.radonshadow.focusdrift.domain.model.FocusSession
import com.radonshadow.focusdrift.domain.model.SessionType
import kotlinx.coroutines.flow.Flow

interface FocusSessionRepository {
    fun observeSessionsForToday(userId: String): Flow<List<FocusSession>>
    fun observeAllSessions(userId: String): Flow<List<FocusSession>>
    suspend fun insertSession(session: FocusSession): Long
    suspend fun countSessionsToday(userId: String): Int
    suspend fun totalFocusMinutesToday(userId: String): Int
    suspend fun lastSessionType(userId: String): SessionType?
}
