package com.radonshadow.focusdrift.core.extensions

import java.util.concurrent.TimeUnit

fun Long.toMMSS(): String {
    val totalSeconds = (this / 1000).coerceAtLeast(0)
    val minutes = TimeUnit.SECONDS.toMinutes(totalSeconds)
    val seconds = totalSeconds - TimeUnit.MINUTES.toSeconds(minutes)
    return "%02d:%02d".format(minutes, seconds)
}

fun Long.toMinutes(): Int = TimeUnit.MILLISECONDS.toMinutes(this).toInt()

fun Int.minutesToMillis(): Long = TimeUnit.MINUTES.toMillis(this.toLong())

fun Long.toHoursMinutesLabel(): String {
    val totalMinutes = toMinutes()
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60
    return if (hours > 0) "${hours}h${minutes}" else "${minutes}m"
}
