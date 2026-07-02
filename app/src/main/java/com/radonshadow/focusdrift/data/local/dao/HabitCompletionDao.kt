package com.radonshadow.focusdrift.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.radonshadow.focusdrift.data.local.entity.HabitCompletionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCompletionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(completion: HabitCompletionEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM habit_completions WHERE habitId = :habitId AND completedDate = :date)")
    suspend fun isCompletedOn(habitId: Long, date: String): Boolean

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY completedDate DESC")
    fun observeCompletions(habitId: Long): Flow<List<HabitCompletionEntity>>

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY completedDate DESC")
    suspend fun getCompletions(habitId: Long): List<HabitCompletionEntity>

    @Query("SELECT completedDate FROM habit_completions WHERE habitId = :habitId AND completedDate BETWEEN :startDate AND :endDate")
    suspend fun getCompletedDatesBetween(habitId: Long, startDate: String, endDate: String): List<String>
}
