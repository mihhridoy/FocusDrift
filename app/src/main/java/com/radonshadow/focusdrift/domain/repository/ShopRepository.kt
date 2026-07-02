package com.radonshadow.focusdrift.domain.repository

import com.radonshadow.focusdrift.domain.model.ShopItem
import kotlinx.coroutines.flow.Flow

interface ShopRepository {
    fun getShopItems(): List<ShopItem>
    fun observeUnlockedItemIds(userId: String): Flow<Set<String>>
    suspend fun isUnlocked(userId: String, itemId: String): Boolean
    suspend fun unlockItem(userId: String, itemId: String)
    suspend fun getSelectedOrbSkinId(userId: String): String
    suspend fun selectOrbSkin(userId: String, itemId: String)
}
