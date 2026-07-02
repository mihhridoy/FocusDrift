package com.radonshadow.focusdrift.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val sessionType: String,
    val plannedDurationMs: Long,
    val actualDurationMs: Long,
    val task: String,
    val startedAt: Long,
    val completedAt: Long?,
    val wasAbandoned: Boolean,
    val xpEarned: Int,
    val coinsEarned: Int
)
