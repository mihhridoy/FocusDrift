package com.radonshadow.focusdrift.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.radonshadow.focusdrift.data.local.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Insert
    suspend fun insert(habit: HabitEntity): Long

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteById(habitId: Long)

    @Query("SELECT * FROM habits WHERE userId = :userId ORDER BY createdAt ASC")
    fun observeHabits(userId: String): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE userId = :userId ORDER BY createdAt ASC")
    suspend fun getHabits(userId: String): List<HabitEntity>

    @Query("SELECT * FROM habits WHERE id = :habitId")
    suspend fun getHabit(habitId: Long): HabitEntity?
}
