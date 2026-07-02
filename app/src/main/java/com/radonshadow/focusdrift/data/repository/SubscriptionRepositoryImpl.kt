package com.radonshadow.focusdrift.data.repository

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.queryProductDetails
import com.android.billingclient.api.queryPurchasesAsync
import com.radonshadow.focusdrift.BuildConfig
import com.radonshadow.focusdrift.core.constants.SubscriptionConstants
import com.radonshadow.focusdrift.domain.model.SubscriptionStatus
import com.radonshadow.focusdrift.domain.model.SubscriptionTier
import com.radonshadow.focusdrift.domain.repository.SubscriptionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SubscriptionRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // Debug builds always report Pro so the app can be reviewed/tested end-to-end without a
    // real Play Billing purchase. Release builds are unaffected — this never ships to users.
    private val status = MutableStateFlow(
        if (BuildConfig.DEBUG) SubscriptionStatus(SubscriptionTier.PRO_LIFETIME) else SubscriptionStatus(SubscriptionTier.FREE)
    )
    private var isConnected = false

    private val purchasesUpdatedListener = PurchasesUpdatedListener { result, purchases ->
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            repositoryScope.launch { purchases.forEach { handlePurchase(it) } }
        }
    }

    private val billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    init {
        repositoryScope.launch { ensureConnected() }
    }

    private suspend fun ensureConnected() {
        if (isConnected) return
        isConnected = connectBillingClient()
        if (isConnected) refreshPurchases()
    }

    private suspend fun connectBillingClient(): Boolean = suspendCancellableCoroutine { cont ->
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (cont.isActive) cont.resumeWith(Result.success(result.responseCode == BillingClient.BillingResponseCode.OK))
            }

            override fun onBillingServiceDisconnected() {
                isConnected = false
            }
        })
    }

    private suspend fun refreshPurchases() {
        val subs = billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        )
        val inApp = billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build()
        )
        (subs.purchasesList + inApp.purchasesList).forEach { handlePurchase(it) }
    }

    private suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return

        val tier = when {
            purchase.products.contains(SubscriptionConstants.PRODUCT_LIFETIME) -> SubscriptionTier.PRO_LIFETIME
            purchase.products.contains(SubscriptionConstants.PRODUCT_YEARLY) -> SubscriptionTier.PRO_YEARLY
            purchase.products.contains(SubscriptionConstants.PRODUCT_MONTHLY) -> SubscriptionTier.PRO_MONTHLY
            else -> return
        }
        status.value = SubscriptionStatus(tier = tier)

        if (!purchase.isAcknowledged) {
            runCatching {
                billingClient.acknowledgePurchase(
                    AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                )
            }
        }
    }

    override fun observeSubscriptionStatus(): Flow<SubscriptionStatus> = status

    override suspend fun getSubscriptionStatus(): SubscriptionStatus {
        ensureConnected()
        return status.value
    }

    override suspend fun launchPurchaseFlow(activity: Activity, productId: String) {
        ensureConnected()
        val productType = if (productId == SubscriptionConstants.PRODUCT_LIFETIME) {
            BillingClient.ProductType.INAPP
        } else {
            BillingClient.ProductType.SUBS
        }

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(productType)
                        .build()
                )
            )
            .build()

        val result = billingClient.queryProductDetails(params)
        val productDetails = result.productDetailsList?.firstOrNull() ?: return

        val productDetailsParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(productDetails)
            .apply {
                if (productType == BillingClient.ProductType.SUBS) {
                    productDetails.subscriptionOfferDetails?.firstOrNull()?.offerToken?.let(::setOfferToken)
                }
            }
            .build()

        val flowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productDetailsParams))
            .build()

        billingClient.launchBillingFlow(activity, flowParams)
    }

    override suspend fun restorePurchases() {
        ensureConnected()
        refreshPurchases()
    }
}
