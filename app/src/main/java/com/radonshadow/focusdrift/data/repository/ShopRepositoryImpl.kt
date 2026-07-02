package com.radonshadow.focusdrift.data.repository

import android.content.Context
import com.radonshadow.focusdrift.R
import com.radonshadow.focusdrift.data.local.dao.UnlockedItemDao
import com.radonshadow.focusdrift.data.local.entity.UnlockedItemEntity
import com.radonshadow.focusdrift.data.local.preferences.UserPreferences
import com.radonshadow.focusdrift.domain.model.ShopCategory
import com.radonshadow.focusdrift.domain.model.ShopItem
import com.radonshadow.focusdrift.domain.repository.ShopRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val unlockedItemDao: UnlockedItemDao,
    private val userPreferences: UserPreferences
) : ShopRepository {

    private val items: List<ShopItem> by lazy { loadShopItems() }

    private fun loadShopItems(): List<ShopItem> {
        val json = context.resources.openRawResource(R.raw.shop_items).bufferedReader().use { it.readText() }
        val root = JSONObject(json)
        val array = root.getJSONArray("items")
        return buildList {
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                add(
                    ShopItem(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        category = ShopCategory.valueOf(obj.getString("category")),
                        description = obj.getString("description"),
                        costCoins = obj.getInt("cost_coins"),
                        previewColor = obj.optString("preview_color", null),
                        glowColor = obj.optString("glow_color", null),
                        primaryColor = obj.optString("primary_color", null),
                        accentColor = obj.optString("accent_color", null)
                    )
                )
            }
        }
    }

    override fun getShopItems(): List<ShopItem> = items

    override fun observeUnlockedItemIds(userId: String): Flow<Set<String>> =
        unlockedItemDao.observeUnlockedIds(userId).map { it.toSet() }

    override suspend fun isUnlocked(userId: String, itemId: String): Boolean =
        unlockedItemDao.isUnlocked(userId, itemId)

    override suspend fun unlockItem(userId: String, itemId: String) {
        unlockedItemDao.insert(UnlockedItemEntity(userId = userId, itemId = itemId, unlockedAt = System.currentTimeMillis()))
    }

    override suspend fun getSelectedOrbSkinId(userId: String): String = userPreferences.selectedOrbSkinId.first()

    override suspend fun selectOrbSkin(userId: String, itemId: String) = userPreferences.setSelectedOrbSkin(itemId)
}
