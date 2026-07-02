package com.radonshadow.focusdrift.core.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

fun <T> Flow<T>.stateInViewModel(
    scope: CoroutineScope,
    initialValue: T,
    stopTimeoutMs: Long = 5000L
): StateFlow<T> = stateIn(
    scope = scope,
    started = SharingStarted.WhileSubscribed(stopTimeoutMs),
    initialValue = initialValue
)
