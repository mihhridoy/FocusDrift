package com.radonshadow.focusdrift.ui.screens.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radonshadow.focusdrift.core.constants.SubscriptionConstants
import com.radonshadow.focusdrift.core.extensions.stateInViewModel
import com.radonshadow.focusdrift.domain.model.ShopCategory
import com.radonshadow.focusdrift.domain.model.ShopItem
import com.radonshadow.focusdrift.domain.repository.ShopRepository
import com.radonshadow.focusdrift.domain.repository.SubscriptionRepository
import com.radonshadow.focusdrift.domain.usecase.progress.GetUserProgressUseCase
import com.radonshadow.focusdrift.domain.usecase.shop.GetShopItemsUseCase
import com.radonshadow.focusdrift.domain.usecase.shop.GetUnlockedItemsUseCase
import com.radonshadow.focusdrift.domain.usecase.shop.PurchaseItemUseCase
import com.radonshadow.focusdrift.domain.usecase.shop.PurchaseResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShopUiState(
    val coins: Int = 0,
    val selectedCategory: ShopCategory = ShopCategory.ORB_SKINS,
    val items: List<ShopItem> = emptyList(),
    val unlockedItemIds: Set<String> = emptySet(),
    val isPro: Boolean = false
)

@HiltViewModel
class ShopViewModel @Inject constructor(
    getUserProgressUseCase: GetUserProgressUseCase,
    getUnlockedItemsUseCase: GetUnlockedItemsUseCase,
    private val getShopItemsUseCase: GetShopItemsUseCase,
    private val purchaseItemUseCase: PurchaseItemUseCase,
    private val shopRepository: ShopRepository,
    private val subscriptionRepository: SubscriptionRepository
) : ViewModel() {

    private val selectedCategory = MutableStateFlow(ShopCategory.ORB_SKINS)
    private val purchaseMessage = MutableStateFlow<String?>(null)
    val purchaseMessageFlow: StateFlow<String?> = purchaseMessage.asStateFlow()

    val uiState: StateFlow<ShopUiState> = combine(
        getUserProgressUseCase(),
        getUnlockedItemsUseCase(),
        selectedCategory,
        subscriptionRepository.observeSubscriptionStatus()
    ) { progress, unlocked, category, subscription ->
        ShopUiState(
            coins = progress.coins,
            selectedCategory = category,
            items = getShopItemsUseCase(category),
            unlockedItemIds = unlocked,
            isPro = subscription.isPro
        )
    }.stateInViewModel(viewModelScope, ShopUiState())

    fun selectCategory(category: ShopCategory) {
        selectedCategory.value = category
    }

    fun purchase(itemId: String) {
        val state = uiState.value
        // Newly-unlocked items are gated behind Pro; already-owned items (e.g. the always-free
        // default orb skin) stay selectable so a downgraded/free user never loses what they had.
        val alreadyOwned = itemId in state.unlockedItemIds
        if (!SubscriptionConstants.FREE_SHOP_ITEMS && !state.isPro && !alreadyOwned) {
            purchaseMessage.value = "Shop unlocks are a Pro feature. Upgrade to Pro to spend your coins here."
            return
        }
        viewModelScope.launch {
            when (purchaseItemUseCase(itemId)) {
                PurchaseResult.Success -> {
                    purchaseMessage.value = "Unlocked!"
                    if (uiState.value.selectedCategory == ShopCategory.ORB_SKINS) {
                        shopRepository.selectOrbSkin(userId = com.radonshadow.focusdrift.core.constants.AppConstants.DEFAULT_USER_ID, itemId = itemId)
                    }
                }
                PurchaseResult.AlreadyOwned -> {
                    if (uiState.value.selectedCategory == ShopCategory.ORB_SKINS) {
                        shopRepository.selectOrbSkin(userId = com.radonshadow.focusdrift.core.constants.AppConstants.DEFAULT_USER_ID, itemId = itemId)
                    }
                }
                PurchaseResult.InsufficientCoins -> purchaseMessage.value = "Not enough coins yet"
            }
        }
    }

    fun clearPurchaseMessage() {
        purchaseMessage.value = null
    }
}
