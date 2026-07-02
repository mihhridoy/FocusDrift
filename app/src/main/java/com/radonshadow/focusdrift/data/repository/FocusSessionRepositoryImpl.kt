package com.radonshadow.focusdrift.data.repository

import com.radonshadow.focusdrift.data.local.dao.FocusSessionDao
import com.radonshadow.focusdrift.data.mapper.toDomain
import com.radonshadow.focusdrift.data.mapper.toEntity
import com.radonshadow.focusdrift.domain.model.FocusSession
import com.radonshadow.focusdrift.domain.model.SessionType
import com.radonshadow.focusdrift.domain.repository.FocusSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

private fun startOfTodayMs(): Long =
    LocalDate.now(ZoneId.systemDefault()).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

class FocusSessionRepositoryImpl @Inject constructor(
    private val dao: FocusSessionDao
) : FocusSessionRepository {

    override fun observeSessionsForToday(userId: String): Flow<List<FocusSession>> =
        dao.observeSessionsSince(userId, startOfTodayMs()).map { list -> list.map { it.toDomain() } }

    override fun observeAllSessions(userId: String): Flow<List<FocusSession>> =
        dao.observeAllSessions(userId).map { list -> list.map { it.toDomain() } }

    override suspend fun insertSession(session: FocusSession): Long = dao.insert(session.toEntity())

    override suspend fun countSessionsToday(userId: String): Int = dao.countSessionsSince(userId, startOfTodayMs())

    override suspend fun totalFocusMinutesToday(userId: String): Int =
        (dao.totalFocusMsSince(userId, startOfTodayMs()) / 60_000L).toInt()

    override suspend fun lastSessionType(userId: String): SessionType? =
        dao.lastSessionType(userId)?.let { runCatching { SessionType.valueOf(it) }.getOrNull() }
}
