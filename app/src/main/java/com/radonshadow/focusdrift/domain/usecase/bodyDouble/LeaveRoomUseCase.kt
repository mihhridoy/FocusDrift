package com.radonshadow.focusdrift.domain.usecase.bodyDouble

import com.radonshadow.focusdrift.domain.repository.BodyDoubleRepository
import javax.inject.Inject

class LeaveRoomUseCase @Inject constructor(
    private val bodyDoubleRepository: BodyDoubleRepository
) {
    /** [participantId] is the Firebase anonymous auth uid used to join the room, not the local user id. */
    suspend operator fun invoke(roomId: String, participantId: String) =
        bodyDoubleRepository.leaveRoom(roomId, participantId)
}
