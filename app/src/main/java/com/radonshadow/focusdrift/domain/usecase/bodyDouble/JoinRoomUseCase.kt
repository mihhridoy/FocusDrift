package com.radonshadow.focusdrift.domain.usecase.bodyDouble

import com.radonshadow.focusdrift.core.constants.AppConstants
import com.radonshadow.focusdrift.core.constants.RewardConstants
import com.radonshadow.focusdrift.domain.model.Participant
import com.radonshadow.focusdrift.domain.model.ParticipantStatus
import com.radonshadow.focusdrift.domain.repository.BodyDoubleRepository
import com.radonshadow.focusdrift.domain.repository.UserProgressRepository
import javax.inject.Inject

/**
 * [participantId] is the Firebase-facing room identity (the anonymous auth uid — unique across
 * devices), while XP/coins are always credited to the single local [AppConstants.DEFAULT_USER_ID]
 * progress record, since that's what Home/Profile read. The two ids are intentionally different.
 */
class JoinRoomUseCase @Inject constructor(
    private val bodyDoubleRepository: BodyDoubleRepository,
    private val userProgressRepository: UserProgressRepository
) {
    suspend operator fun invoke(
        roomId: String,
        displayName: String,
        avatarColor: String,
        currentTask: String,
        participantId: String
    ) {
        val now = System.currentTimeMillis()
        bodyDoubleRepository.joinRoom(
            roomId,
            Participant(
                userId = participantId,
                displayName = displayName,
                avatarColor = avatarColor,
                status = ParticipantStatus.FOCUSING,
                currentTask = currentTask,
                joinedAt = now,
                lastSeen = now
            )
        )
        userProgressRepository.awardXp(AppConstants.DEFAULT_USER_ID, RewardConstants.XP_PER_BODY_DOUBLE_SESSION)
        userProgressRepository.awardCoins(AppConstants.DEFAULT_USER_ID, RewardConstants.COINS_PER_BODY_DOUBLE)
    }
}
