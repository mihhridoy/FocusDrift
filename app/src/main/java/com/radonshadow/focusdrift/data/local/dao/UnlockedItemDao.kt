package com.radonshadow.focusdrift.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.radonshadow.focusdrift.data.local.entity.UnlockedItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UnlockedItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: UnlockedItemEntity)

    @Query("SELECT itemId FROM unlocked_items WHERE userId = :userId")
    fun observeUnlockedIds(userId: String): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM unlocked_items WHERE userId = :userId AND itemId = :itemId)")
    suspend fun isUnlocked(userId: String, itemId: String): Boolean
}
