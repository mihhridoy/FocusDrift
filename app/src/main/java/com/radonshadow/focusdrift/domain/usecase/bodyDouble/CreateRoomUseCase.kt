package com.radonshadow.focusdrift.domain.usecase.bodyDouble

import com.radonshadow.focusdrift.domain.repository.BodyDoubleRepository
import javax.inject.Inject

class CreateRoomUseCase @Inject constructor(
    private val bodyDoubleRepository: BodyDoubleRepository
) {
    /** [createdBy] is the Firebase anonymous auth uid, not the local user id. */
    suspend operator fun invoke(
        name: String,
        type: String,
        sessionDurationMs: Long,
        createdBy: String
    ): String = bodyDoubleRepository.createRoom(name, type, sessionDurationMs, createdBy)
}
