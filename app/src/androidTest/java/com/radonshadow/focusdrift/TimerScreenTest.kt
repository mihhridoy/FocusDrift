package com.radonshadow.focusdrift

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.radonshadow.focusdrift.ui.components.DriftButton
import com.radonshadow.focusdrift.ui.components.FocusOrb
import com.radonshadow.focusdrift.ui.components.FocusOrbState
import com.radonshadow.focusdrift.ui.components.SessionControlsBar
import com.radonshadow.focusdrift.ui.theme.FocusDriftTheme
import org.junit.Rule
import org.junit.Test

/**
 * Exercises the timer screen's building blocks directly (rather than the Hilt-backed
 * [com.radonshadow.focusdrift.ui.screens.timer.TimerScreen]) so the test doesn't need a full DI
 * graph (Room, Firebase, the foreground service) wired up under instrumentation.
 */
class TimerScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun driftButton_showsAcknowledgementCopy_andInvokesCallbackOnTap() {
        var tapped = false
        composeRule.setContent {
            FocusDriftTheme {
                DriftButton(isDrifting = false, onClick = { tapped = true })
            }
        }

        composeRule.onNodeWithText("I'm drifting").assertExists()
        composeRule.onNodeWithText("I'm drifting").performClick()
        assert(tapped)
    }

    @Test
    fun driftButton_whenDrifting_offersAResumePath() {
        composeRule.setContent {
            FocusDriftTheme {
                DriftButton(isDrifting = true, onClick = {})
            }
        }

        composeRule.onNodeWithText("It's okay — resume").assertExists()
    }

    @Test
    fun sessionControlsBar_rendersWithoutCrashing() {
        composeRule.setContent {
            FocusDriftTheme {
                SessionControlsBar(
                    isPaused = false,
                    onPauseResumeClick = {},
                    onResetClick = {},
                    onCompleteEarlyClick = {}
                )
                FocusOrb(state = FocusOrbState.ACTIVE)
            }
        }
    }
}
