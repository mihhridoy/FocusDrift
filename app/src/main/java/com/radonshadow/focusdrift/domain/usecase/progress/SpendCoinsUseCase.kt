package com.radonshadow.focusdrift.domain.usecase.progress

import com.radonshadow.focusdrift.core.constants.AppConstants
import com.radonshadow.focusdrift.domain.repository.UserProgressRepository
import javax.inject.Inject

class SpendCoinsUseCase @Inject constructor(
    private val userProgressRepository: UserProgressRepository
) {
    suspend operator fun invoke(amount: Int, userId: String = AppConstants.DEFAULT_USER_ID): Boolean =
        userProgressRepository.spendCoins(userId, amount)
}
