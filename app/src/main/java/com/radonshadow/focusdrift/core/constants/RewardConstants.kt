package com.radonshadow.focusdrift.core.constants

object RewardConstants {
    const val XP_PER_FOCUS_SESSION = 50
    const val XP_PER_HABIT_COMPLETE = 20
    const val XP_PER_BODY_DOUBLE_SESSION = 30
    const val XP_DAILY_GOAL_BONUS = 100
    const val XP_STREAK_7_DAY_BONUS = 200

    const val COINS_PER_SESSION = 50
    const val COINS_PER_HABIT = 10
    const val COINS_PER_BODY_DOUBLE = 30
    const val COINS_DAILY_GOAL_BONUS = 100
    const val COINS_STREAK_7_DAY_BONUS = 200

    fun xpForLevel(level: Int): Int = level * 100

    /** Returns (level, xpIntoCurrentLevel, xpRequiredForNextLevel) for a given lifetime XP total. */
    fun levelProgressFor(totalXp: Int): Triple<Int, Int, Int> {
        var level = 1
        var xpConsumed = 0
        while (xpConsumed + xpForLevel(level) <= totalXp) {
            xpConsumed += xpForLevel(level)
            level++
        }
        return Triple(level, totalXp - xpConsumed, xpForLevel(level))
    }
}
