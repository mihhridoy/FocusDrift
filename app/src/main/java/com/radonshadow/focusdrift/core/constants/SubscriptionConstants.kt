package com.radonshadow.focusdrift.core.constants

object SubscriptionConstants {
    const val PRODUCT_MONTHLY = "focusdrift_pro_monthly"
    const val PRODUCT_YEARLY = "focusdrift_pro_yearly"
    const val PRODUCT_LIFETIME = "focusdrift_pro_lifetime"

    const val PRICE_MONTHLY_DISPLAY = "$8.99"
    const val PRICE_YEARLY_DISPLAY = "$59.99"
    const val PRICE_LIFETIME_DISPLAY = "$129"

    const val FREE_TRIAL_DAYS = 7

    // Free tier limits
    const val FREE_SESSIONS_PER_DAY = 5
    const val FREE_BODY_DOUBLE_ROOMS = false
    const val FREE_STREAK_GRACE_DAYS = 0
    const val FREE_HABITS_MAX = 3
    const val FREE_SHOP_ITEMS = false

    // Pro unlocks
    const val PRO_SESSIONS_PER_DAY = Int.MAX_VALUE
    const val PRO_STREAK_GRACE_DAYS = 3
    const val PRO_HABITS_MAX = Int.MAX_VALUE
}
