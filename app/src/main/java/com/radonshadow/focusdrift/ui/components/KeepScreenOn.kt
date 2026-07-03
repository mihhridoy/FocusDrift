package com.radonshadow.focusdrift.ui.components

import android.app.Activity
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView

/**
 * Keeps the device screen awake while [enabled] is true — without this, a long-running focus
 * session (or a slow read through onboarding) outlasts most devices' short screen-timeout, and
 * the display dims and locks mid-session. That looked like the app "closing" or the Focus tab
 * rendering a washed-out "dark shadow" (the OS's pre-sleep dim animation), when really the phone
 * had just gone to sleep underneath a still-running app.
 */
@Composable
fun KeepScreenOn(enabled: Boolean) {
    val view = LocalView.current
    val context = LocalContext.current

    DisposableEffect(enabled) {
        val activity = context as? Activity
        if (enabled) {
            view.keepScreenOn = true
            activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        onDispose {
            view.keepScreenOn = false
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
