package com.radonshadow.focusdrift.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.radonshadow.focusdrift.data.local.entity.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIfAbsent(progress: UserProgressEntity)

    @Update
    suspend fun update(progress: UserProgressEntity)

    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    fun observe(userId: String): Flow<UserProgressEntity?>

    @Query("SELECT * FROM user_progress WHERE userId = :userId")
    suspend fun get(userId: String): UserProgressEntity?
}
