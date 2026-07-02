package com.radonshadow.focusdrift.domain.repository

import com.radonshadow.focusdrift.domain.model.UserProgress
import com.radonshadow.focusdrift.domain.model.XpReward
import kotlinx.coroutines.flow.Flow

interface UserProgressRepository {
    fun observeUserProgress(userId: String): Flow<UserProgress>
    suspend fun getUserProgress(userId: String): UserProgress
    suspend fun awardXp(userId: String, amount: Int): XpReward
    suspend fun awardCoins(userId: String, amount: Int)
    suspend fun spendCoins(userId: String, amount: Int): Boolean
    suspend fun incrementStreak(userId: String)
    suspend fun resetStreak(userId: String)
    suspend fun recordSessionCompleted(userId: String, focusMinutes: Int)
    suspend fun resetDailyCounters(userId: String)
}
