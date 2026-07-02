package com.radonshadow.focusdrift.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.radonshadow.focusdrift.core.extensions.stateInViewModel
import com.radonshadow.focusdrift.data.local.preferences.UserPreferences
import com.radonshadow.focusdrift.domain.model.SubscriptionStatus
import com.radonshadow.focusdrift.domain.model.SubscriptionTier
import com.radonshadow.focusdrift.domain.model.UserProgress
import com.radonshadow.focusdrift.domain.repository.SubscriptionRepository
import com.radonshadow.focusdrift.domain.usecase.progress.GetUserProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class ProfileUiState(
    val displayName: String = "You",
    val userProgress: UserProgress = UserProgress(userId = ""),
    val subscriptionStatus: SubscriptionStatus = SubscriptionStatus(SubscriptionTier.FREE)
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getUserProgressUseCase: GetUserProgressUseCase,
    subscriptionRepository: SubscriptionRepository,
    userPreferences: UserPreferences
) : ViewModel() {

    val uiState = combine(
        userPreferences.displayName,
        getUserProgressUseCase(),
        subscriptionRepository.observeSubscriptionStatus()
    ) { name, progress, subscription ->
        ProfileUiState(displayName = name, userProgress = progress, subscriptionStatus = subscription)
    }.stateInViewModel(viewModelScope, ProfileUiState())
}
