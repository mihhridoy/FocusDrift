package com.radonshadow.focusdrift.domain.repository

import android.app.Activity
import com.radonshadow.focusdrift.domain.model.SubscriptionPricing
import com.radonshadow.focusdrift.domain.model.SubscriptionStatus
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun observeSubscriptionStatus(): Flow<SubscriptionStatus>
    suspend fun getSubscriptionStatus(): SubscriptionStatus
    fun observePricing(): Flow<SubscriptionPricing>
    suspend fun launchPurchaseFlow(activity: Activity, productId: String)
    suspend fun restorePurchases()
}
