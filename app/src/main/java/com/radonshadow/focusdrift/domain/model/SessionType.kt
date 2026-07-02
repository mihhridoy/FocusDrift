package com.radonshadow.focusdrift.domain.model

enum class SessionType(val defaultDurationMs: Long, val label: String) {
    FOCUS(25 * 60 * 1000L, "Focus Session"),
    SHORT_BREAK(5 * 60 * 1000L, "Short Break"),
    LONG_BREAK(15 * 60 * 1000L, "Long Break")
}
