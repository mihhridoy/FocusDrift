package com.radonshadow.focusdrift.domain.model

/**
 * Prices as formatted and localized by Google Play for the user's actual country/currency
 * (e.g. "$8.99", "€8,49", "₹749.00"). Null while unfetched -- callers should show a loading
 * placeholder rather than fall back to a hardcoded currency, since a mismatch between this
 * screen's displayed price and the price Play actually charges is a Subscriptions policy
 * violation.
 */
data class SubscriptionPricing(
    val monthly: String? = null,
    val yearly: String? = null,
    val lifetime: String? = null
)
