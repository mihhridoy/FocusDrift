package com.radonshadow.focusdrift.domain.usecase.shop

import com.radonshadow.focusdrift.domain.model.ShopCategory
import com.radonshadow.focusdrift.domain.model.ShopItem
import com.radonshadow.focusdrift.domain.repository.ShopRepository
import javax.inject.Inject

class GetShopItemsUseCase @Inject constructor(
    private val shopRepository: ShopRepository
) {
    operator fun invoke(category: ShopCategory? = null): List<ShopItem> {
        val items = shopRepository.getShopItems()
        return if (category == null) items else items.filter { it.category == category }
    }
}
