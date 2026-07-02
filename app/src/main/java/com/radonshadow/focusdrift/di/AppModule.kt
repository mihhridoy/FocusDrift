package com.radonshadow.focusdrift.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.radonshadow.focusdrift.core.constants.AppConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserPrefsDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class TimerPrefsDataStore

private val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = AppConstants.USER_PREFERENCES_NAME)
private val Context.timerDataStore: DataStore<Preferences> by preferencesDataStore(name = AppConstants.TIMER_PREFERENCES_NAME)

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @UserPrefsDataStore
    fun provideUserPreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.userDataStore

    @Provides
    @Singleton
    @TimerPrefsDataStore
    fun provideTimerPreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.timerDataStore
}
