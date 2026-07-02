package com.radonshadow.focusdrift.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val name: String,
    val emoji: String,
    val frequency: String,
    val customDays: String, // comma-separated ints, e.g. "1,3,5"
    val reminderEnabled: Boolean,
    val reminderHour: Int,
    val reminderMinute: Int,
    val createdAt: Long,
    val colorTint: String
)
