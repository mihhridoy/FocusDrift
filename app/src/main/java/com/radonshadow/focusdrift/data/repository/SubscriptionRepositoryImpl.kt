package com.radonshadow.focusdrift.data.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
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
import kotlinx.coroutines.CoroutineExceptionHandler
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

    // Play Billing is unavailable on plenty of real devices (no Play Store/Play Services, e.g.
    // sideloaded test builds) and every call here does I/O across a binder connection to the
    // Play Store app — none of that should ever be allowed to crash the whole app, since a
    // missing/broken billing connection just means "treat this device as not-yet-purchased".
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("SubscriptionRepository", "Unhandled billing error", throwable)
    }
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO + exceptionHandler)

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

    private val billingClient: BillingClient? = runCatching {
        BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            // The no-arg overload was removed in Billing 8; one-time products (the Lifetime
            // tier) are the only pending-purchase-eligible thing this app sells.
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .build()
    }.onFailure { Log.e("SubscriptionRepository", "BillingClient could not be created", it) }.getOrNull()

    init {
        repositoryScope.launch { ensureConnected() }
    }

    private suspend fun ensureConnected() {
        if (isConnected || billingClient == null) return
        isConnected = runCatching { connectBillingClient() }.getOrDefault(false)
        if (isConnected) runCatching { refreshPurchases() }
    }

    private suspend fun connectBillingClient(): Boolean = suspendCancellableCoroutine { cont ->
        val client = billingClient ?: run { cont.resumeWith(Result.success(false)); return@suspendCancellableCoroutine }
        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (cont.isActive) cont.resumeWith(Result.success(result.responseCode == BillingClient.BillingResponseCode.OK))
            }

            override fun onBillingServiceDisconnected() {
                isConnected = false
            }
        })
    }

    private suspend fun refreshPurchases() {
        val client = billingClient ?: return
        val subs = client.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.SUBS).build()
        )
        val inApp = client.queryPurchasesAsync(
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
                billingClient?.acknowledgePurchase(
                    AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                )
            }
        }
    }

    override fun observeSubscriptionStatus(): Flow<SubscriptionStatus> = status

    override suspend fun getSubscriptionStatus(): SubscriptionStatus {
        runCatching { ensureConnected() }
        return status.value
    }

    override suspend fun launchPurchaseFlow(activity: Activity, productId: String) {
        val client = billingClient ?: return
        runCatching { ensureConnected() }
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

        val result = client.queryProductDetails(params)
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

        client.launchBillingFlow(activity, flowParams)
    }

    override suspend fun restorePurchases() {
        runCatching {
            ensureConnected()
            refreshPurchases()
        }
    }
}
