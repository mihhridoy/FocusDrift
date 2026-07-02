package com.radonshadow.focusdrift.data.repository

import com.radonshadow.focusdrift.core.constants.RewardConstants
import com.radonshadow.focusdrift.data.local.dao.UserProgressDao
import com.radonshadow.focusdrift.data.local.entity.UserProgressEntity
import com.radonshadow.focusdrift.domain.model.UserProgress
import com.radonshadow.focusdrift.domain.model.XpReward
import com.radonshadow.focusdrift.domain.repository.UserProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private val DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE
private fun today(): String = LocalDate.now().format(DATE_FORMATTER)

class UserProgressRepositoryImpl @Inject constructor(
    private val dao: UserProgressDao
) : UserProgressRepository {

    private suspend fun ensureRow(userId: String): UserProgressEntity {
        dao.insertIfAbsent(UserProgressEntity(userId = userId, lastActiveDate = today()))
        val row = dao.get(userId) ?: UserProgressEntity(userId = userId, lastActiveDate = today())
        return if (row.lastActiveDate != today()) {
            val reset = row.copy(sessionsToday = 0, xpEarnedToday = 0, focusMinutesToday = 0, lastActiveDate = today())
            dao.update(reset)
            reset
        } else {
            row
        }
    }

    override fun observeUserProgress(userId: String): Flow<UserProgress> =
        dao.observe(userId).map { entity -> (entity ?: UserProgressEntity(userId = userId)).toDomain() }

    override suspend fun getUserProgress(userId: String): UserProgress = ensureRow(userId).toDomain()

    override suspend fun awardXp(userId: String, amount: Int): XpReward {
        val before = ensureRow(userId)
        val (levelBefore, _, _) = RewardConstants.levelProgressFor(before.totalXp)
        val newTotalXp = before.totalXp + amount
        val (levelAfter, _, _) = RewardConstants.levelProgressFor(newTotalXp)
        dao.update(before.copy(totalXp = newTotalXp, xpEarnedToday = before.xpEarnedToday + amount))
        return XpReward(
            xpEarned = amount,
            coinsEarned = 0,
            xpBefore = before.totalXp,
            xpAfter = newTotalXp,
            levelBefore = levelBefore,
            levelAfter = levelAfter,
            leveledUp = levelAfter > levelBefore
        )
    }

    override suspend fun awardCoins(userId: String, amount: Int) {
        val row = ensureRow(userId)
        dao.update(row.copy(coins = row.coins + amount))
    }

    override suspend fun spendCoins(userId: String, amount: Int): Boolean {
        val row = ensureRow(userId)
        if (row.coins < amount) return false
        dao.update(row.copy(coins = row.coins - amount))
        return true
    }

    override suspend fun incrementStreak(userId: String) {
        val row = ensureRow(userId)
        val newStreak = row.currentStreak + 1
        dao.update(row.copy(currentStreak = newStreak, longestStreak = maxOf(row.longestStreak, newStreak)))
    }

    override suspend fun resetStreak(userId: String) {
        val row = ensureRow(userId)
        dao.update(row.copy(currentStreak = 0))
    }

    override suspend fun recordSessionCompleted(userId: String, focusMinutes: Int) {
        val row = ensureRow(userId)
        dao.update(
            row.copy(
                totalSessions = row.totalSessions + 1,
                totalFocusMinutes = row.totalFocusMinutes + focusMinutes,
                sessionsToday = row.sessionsToday + 1,
                focusMinutesToday = row.focusMinutesToday + focusMinutes
            )
        )
    }

    override suspend fun resetDailyCounters(userId: String) {
        val row = ensureRow(userId)
        dao.update(row.copy(sessionsToday = 0, xpEarnedToday = 0, focusMinutesToday = 0, lastActiveDate = today()))
    }
}

private fun UserProgressEntity.toDomain(): UserProgress {
    val (level, xpIntoLevel, xpToNextLevel) = RewardConstants.levelProgressFor(totalXp)
    return UserProgress(
        userId = userId,
        level = level,
        totalXp = totalXp,
        xpIntoLevel = xpIntoLevel,
        xpToNextLevel = xpToNextLevel,
        coins = coins,
        totalSessions = totalSessions,
        totalFocusMinutes = totalFocusMinutes,
        sessionsToday = sessionsToday,
        dailyGoalSessions = dailyGoalSessions,
        xpEarnedToday = xpEarnedToday,
        focusMinutesToday = focusMinutesToday,
        currentStreak = currentStreak,
        longestStreak = longestStreak
    )
}
