package com.radonshadow.focusdrift.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.radonshadow.focusdrift.data.local.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {

    @Insert
    suspend fun insert(session: FocusSessionEntity): Long

    @Query("SELECT * FROM focus_sessions WHERE userId = :userId ORDER BY startedAt DESC")
    fun observeAllSessions(userId: String): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE userId = :userId AND startedAt >= :startOfDayMs ORDER BY startedAt DESC")
    fun observeSessionsSince(userId: String, startOfDayMs: Long): Flow<List<FocusSessionEntity>>

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE userId = :userId AND startedAt >= :startOfDayMs AND wasAbandoned = 0 AND sessionType = 'FOCUS'")
    suspend fun countSessionsSince(userId: String, startOfDayMs: Long): Int

    @Query("SELECT COALESCE(SUM(actualDurationMs), 0) FROM focus_sessions WHERE userId = :userId AND startedAt >= :startOfDayMs AND wasAbandoned = 0 AND sessionType = 'FOCUS'")
    suspend fun totalFocusMsSince(userId: String, startOfDayMs: Long): Long

    @Query("SELECT sessionType FROM focus_sessions WHERE userId = :userId ORDER BY startedAt DESC LIMIT 1")
    suspend fun lastSessionType(userId: String): String?
}
