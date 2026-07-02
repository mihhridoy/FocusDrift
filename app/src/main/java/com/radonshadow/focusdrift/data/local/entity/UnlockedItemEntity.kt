package com.radonshadow.focusdrift.data.local.entity

import androidx.room.Entity

@Entity(tableName = "unlocked_items", primaryKeys = ["userId", "itemId"])
data class UnlockedItemEntity(
    val userId: String,
    val itemId: String,
    val unlockedAt: Long
)
