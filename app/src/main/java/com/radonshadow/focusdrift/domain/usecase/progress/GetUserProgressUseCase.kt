package com.radonshadow.focusdrift.domain.usecase.progress

import com.radonshadow.focusdrift.core.constants.AppConstants
import com.radonshadow.focusdrift.domain.model.UserProgress
import com.radonshadow.focusdrift.domain.repository.UserProgressRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProgressUseCase @Inject constructor(
    private val userProgressRepository: UserProgressRepository
) {
    operator fun invoke(userId: String = AppConstants.DEFAULT_USER_ID): Flow<UserProgress> =
        userProgressRepository.observeUserProgress(userId)
}
