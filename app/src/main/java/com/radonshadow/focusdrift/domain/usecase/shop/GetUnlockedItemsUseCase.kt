package com.radonshadow.focusdrift.domain.usecase.shop

import com.radonshadow.focusdrift.core.constants.AppConstants
import com.radonshadow.focusdrift.domain.repository.ShopRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUnlockedItemsUseCase @Inject constructor(
    private val shopRepository: ShopRepository
) {
    operator fun invoke(userId: String = AppConstants.DEFAULT_USER_ID): Flow<Set<String>> =
        shopRepository.observeUnlockedItemIds(userId)
}
