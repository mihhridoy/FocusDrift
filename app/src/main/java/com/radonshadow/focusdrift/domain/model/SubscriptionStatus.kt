package com.radonshadow.focusdrift.domain.model

enum class SubscriptionTier {
    FREE,
    PRO_MONTHLY,
    PRO_YEARLY,
    PRO_LIFETIME
}

data class SubscriptionStatus(
    val tier: SubscriptionTier,
    val isTrialActive: Boolean = false,
    val trialEndsAt: Long? = null,
    val expiresAt: Long? = null
) {
    val isPro: Boolean get() = tier != SubscriptionTier.FREE || isTrialActive
}
