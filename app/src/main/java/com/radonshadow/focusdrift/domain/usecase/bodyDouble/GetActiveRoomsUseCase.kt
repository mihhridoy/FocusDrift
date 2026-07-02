package com.radonshadow.focusdrift.domain.usecase.bodyDouble

import com.radonshadow.focusdrift.domain.model.BodyDoubleRoom
import com.radonshadow.focusdrift.domain.repository.BodyDoubleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetActiveRoomsUseCase @Inject constructor(
    private val bodyDoubleRepository: BodyDoubleRepository
) {
    operator fun invoke(): Flow<List<BodyDoubleRoom>> = bodyDoubleRepository.observeActiveRooms()
}
