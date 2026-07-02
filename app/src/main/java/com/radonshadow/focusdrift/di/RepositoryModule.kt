package com.radonshadow.focusdrift.di

import com.radonshadow.focusdrift.data.repository.BodyDoubleRepositoryImpl
import com.radonshadow.focusdrift.data.repository.FocusSessionRepositoryImpl
import com.radonshadow.focusdrift.data.repository.HabitRepositoryImpl
import com.radonshadow.focusdrift.data.repository.ShopRepositoryImpl
import com.radonshadow.focusdrift.data.repository.SubscriptionRepositoryImpl
import com.radonshadow.focusdrift.data.repository.UserProgressRepositoryImpl
import com.radonshadow.focusdrift.domain.repository.BodyDoubleRepository
import com.radonshadow.focusdrift.domain.repository.FocusSessionRepository
import com.radonshadow.focusdrift.domain.repository.FocusTimerController
import com.radonshadow.focusdrift.domain.repository.HabitRepository
import com.radonshadow.focusdrift.domain.repository.ShopRepository
import com.radonshadow.focusdrift.domain.repository.SubscriptionRepository
import com.radonshadow.focusdrift.domain.repository.UserProgressRepository
import com.radonshadow.focusdrift.service.TimerServiceConnection
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFocusSessionRepository(impl: FocusSessionRepositoryImpl): FocusSessionRepository

    @Binds
    @Singleton
    abstract fun bindHabitRepository(impl: HabitRepositoryImpl): HabitRepository

    @Binds
    @Singleton
    abstract fun bindUserProgressRepository(impl: UserProgressRepositoryImpl): UserProgressRepository

    @Binds
    @Singleton
    abstract fun bindBodyDoubleRepository(impl: BodyDoubleRepositoryImpl): BodyDoubleRepository

    @Binds
    @Singleton
    abstract fun bindShopRepository(impl: ShopRepositoryImpl): ShopRepository

    @Binds
    @Singleton
    abstract fun bindSubscriptionRepository(impl: SubscriptionRepositoryImpl): SubscriptionRepository

    @Binds
    @Singleton
    abstract fun bindFocusTimerController(impl: TimerServiceConnection): FocusTimerController
}
