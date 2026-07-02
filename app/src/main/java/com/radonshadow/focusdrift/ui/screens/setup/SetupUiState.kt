package com.radonshadow.focusdrift.ui.screens.setup

data class DriftTimeOption(val id: String, val label: String)
data class FocusKillerOption(val id: String, val label: String)

val DRIFT_TIME_OPTIONS = listOf(
    DriftTimeOption("morning", "Morning"),
    DriftTimeOption("afternoon", "Afternoon"),
    DriftTimeOption("evening", "Evening"),
    DriftTimeOption("late_night", "Late night")
)

val FOCUS_KILLER_OPTIONS = listOf(
    FocusKillerOption("notifications", "Notifications"),
    FocusKillerOption("intrusive_thoughts", "Intrusive thoughts"),
    FocusKillerOption("noise", "Noise"),
    FocusKillerOption("low_energy", "Low energy"),
    FocusKillerOption("overwhelm", "Overwhelm"),
    FocusKillerOption("urge_to_clean", "Urge to clean")
)

data class SetupUiState(
    val selectedDriftTimes: Set<String> = setOf("morning", "evening"),
    val selectedFocusKillers: Set<String> = setOf("notifications", "low_energy"),
    val focusMinutes: Int = 20,
    val isSaving: Boolean = false
)
