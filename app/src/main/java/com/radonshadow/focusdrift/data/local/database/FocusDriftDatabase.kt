package com.radonshadow.focusdrift.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.radonshadow.focusdrift.data.local.dao.FocusSessionDao
import com.radonshadow.focusdrift.data.local.dao.HabitCompletionDao
import com.radonshadow.focusdrift.data.local.dao.HabitDao
import com.radonshadow.focusdrift.data.local.dao.UnlockedItemDao
import com.radonshadow.focusdrift.data.local.dao.UserProgressDao
import com.radonshadow.focusdrift.data.local.entity.FocusSessionEntity
import com.radonshadow.focusdrift.data.local.entity.HabitCompletionEntity
import com.radonshadow.focusdrift.data.local.entity.HabitEntity
import com.radonshadow.focusdrift.data.local.entity.UnlockedItemEntity
import com.radonshadow.focusdrift.data.local.entity.UserProgressEntity

@Database(
    entities = [
        FocusSessionEntity::class,
        HabitEntity::class,
        HabitCompletionEntity::class,
        UserProgressEntity::class,
        UnlockedItemEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class FocusDriftDatabase : RoomDatabase() {
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun habitDao(): HabitDao
    abstract fun habitCompletionDao(): HabitCompletionDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun unlockedItemDao(): UnlockedItemDao
}
