package com.radonshadow.focusdrift.domain.usecase.shop

import com.radonshadow.focusdrift.core.constants.AppConstants
import com.radonshadow.focusdrift.domain.repository.ShopRepository
import com.radonshadow.focusdrift.domain.repository.UserProgressRepository
import javax.inject.Inject

sealed class PurchaseResult {
    object Success : PurchaseResult()
    object AlreadyOwned : PurchaseResult()
    object InsufficientCoins : PurchaseResult()
}

class PurchaseItemUseCase @Inject constructor(
    private val shopRepository: ShopRepository,
    private val userProgressRepository: UserProgressRepository
) {
    suspend operator fun invoke(itemId: String, userId: String = AppConstants.DEFAULT_USER_ID): PurchaseResult {
        if (shopRepository.isUnlocked(userId, itemId)) return PurchaseResult.AlreadyOwned
        val item = shopRepository.getShopItems().find { it.id == itemId }
            ?: return PurchaseResult.InsufficientCoins

        val spent = userProgressRepository.spendCoins(userId, item.costCoins)
        if (!spent) return PurchaseResult.InsufficientCoins

        shopRepository.unlockItem(userId, itemId)
        return PurchaseResult.Success
    }
}
