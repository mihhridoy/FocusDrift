package com.radonshadow.focusdrift.ui.screens.subscription

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radonshadow.focusdrift.core.constants.SubscriptionConstants
import com.radonshadow.focusdrift.core.extensions.stateInViewModel
import com.radonshadow.focusdrift.domain.model.SubscriptionPricing
import com.radonshadow.focusdrift.domain.model.SubscriptionStatus
import com.radonshadow.focusdrift.domain.model.SubscriptionTier
import com.radonshadow.focusdrift.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    val status = subscriptionRepository.observeSubscriptionStatus()
        .stateInViewModel(viewModelScope, SubscriptionStatus(SubscriptionTier.FREE))

    val pricing = subscriptionRepository.observePricing()
        .stateInViewModel(viewModelScope, SubscriptionPricing())

    fun purchase(activity: Activity, productId: String) {
        viewModelScope.launch { subscriptionRepository.launchPurchaseFlow(activity, productId) }
    }

    fun startFreeTrial(activity: Activity) {
        purchase(activity, SubscriptionConstants.PRODUCT_YEARLY)
    }

    fun restorePurchases() {
        viewModelScope.launch { subscriptionRepository.restorePurchases() }
    }
}
