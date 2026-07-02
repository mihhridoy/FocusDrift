package com.radonshadow.focusdrift.di

import android.content.Context
import androidx.room.Room
import com.radonshadow.focusdrift.core.constants.AppConstants
import com.radonshadow.focusdrift.data.local.dao.FocusSessionDao
import com.radonshadow.focusdrift.data.local.dao.HabitCompletionDao
import com.radonshadow.focusdrift.data.local.dao.HabitDao
import com.radonshadow.focusdrift.data.local.dao.UnlockedItemDao
import com.radonshadow.focusdrift.data.local.dao.UserProgressDao
import com.radonshadow.focusdrift.data.local.database.FocusDriftDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FocusDriftDatabase =
        Room.databaseBuilder(context, FocusDriftDatabase::class.java, AppConstants.DATABASE_NAME).build()

    @Provides
    fun provideFocusSessionDao(db: FocusDriftDatabase): FocusSessionDao = db.focusSessionDao()

    @Provides
    fun provideHabitDao(db: FocusDriftDatabase): HabitDao = db.habitDao()

    @Provides
    fun provideHabitCompletionDao(db: FocusDriftDatabase): HabitCompletionDao = db.habitCompletionDao()

    @Provides
    fun provideUserProgressDao(db: FocusDriftDatabase): UserProgressDao = db.userProgressDao()

    @Provides
    fun provideUnlockedItemDao(db: FocusDriftDatabase): UnlockedItemDao = db.unlockedItemDao()
}
