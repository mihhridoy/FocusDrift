package com.radonshadow.focusdrift.core.utils

import com.radonshadow.focusdrift.core.extensions.minutesToMillis
import com.radonshadow.focusdrift.core.extensions.toHoursMinutesLabel
import com.radonshadow.focusdrift.core.extensions.toMMSS
import com.radonshadow.focusdrift.core.extensions.toMinutes
import org.junit.Assert.assertEquals
import org.junit.Test

class TimeExtensionsTest {

    @Test
    fun `toMMSS formats sub-minute durations`() {
        assertEquals("00:00", 0L.toMMSS())
        assertEquals("00:05", 5_000L.toMMSS())
        assertEquals("00:59", 59_000L.toMMSS())
    }

    @Test
    fun `toMMSS formats minutes and seconds`() {
        assertEquals("01:00", 60_000L.toMMSS())
        assertEquals("19:43", (19 * 60_000L + 43_000L).toMMSS())
        assertEquals("25:00", (25 * 60_000L).toMMSS())
    }

    @Test
    fun `toMMSS clamps negative durations to zero`() {
        assertEquals("00:00", (-500L).toMMSS())
    }

    @Test
    fun `toMinutes converts millis down to whole minutes`() {
        assertEquals(0, 59_000L.toMinutes())
        assertEquals(1, 60_000L.toMinutes())
        assertEquals(25, (25 * 60_000L).toMinutes())
    }

    @Test
    fun `minutesToMillis is the inverse of toMinutes`() {
        assertEquals(25 * 60_000L, 25.minutesToMillis())
    }

    @Test
    fun `toHoursMinutesLabel omits hours when under 60 minutes`() {
        assertEquals("40m", 40.minutesToMillis().toHoursMinutesLabel())
    }

    @Test
    fun `toHoursMinutesLabel includes hours once over 60 minutes`() {
        assertEquals("1h40", 100.minutesToMillis().toHoursMinutesLabel())
    }
}
