package com.radonshadow.focusdrift.domain.usecase.progress

import com.radonshadow.focusdrift.core.constants.RewardConstants
import javax.inject.Inject

data class LevelProgress(val level: Int, val xpIntoLevel: Int, val xpToNextLevel: Int)

/** Pure XP -> level calculation, shared by the repository (on award) and the UI (progress bars). */
class CheckLevelUpUseCase @Inject constructor() {
    operator fun invoke(totalXp: Int): LevelProgress {
        val (level, xpIntoLevel, xpToNextLevel) = RewardConstants.levelProgressFor(totalXp)
        return LevelProgress(level, xpIntoLevel, xpToNextLevel)
    }
}
