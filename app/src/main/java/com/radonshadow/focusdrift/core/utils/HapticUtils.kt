package com.radonshadow.focusdrift.core.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object HapticUtils {

    private fun vibrator(context: Context): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            manager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    private fun vibrate(context: Context, pattern: LongArray, amplitudes: IntArray? = null) {
        val vibrator = vibrator(context)
        if (!vibrator.hasVibrator()) return
        val effect = if (amplitudes != null) {
            VibrationEffect.createWaveform(pattern, amplitudes, -1)
        } else {
            VibrationEffect.createWaveform(pattern, -1)
        }
        vibrator.vibrate(effect)
    }

    /** Short tick for taps, chip selection, drift acknowledgement. */
    fun tap(context: Context) {
        val vibrator = vibrator(context)
        if (!vibrator.hasVibrator()) return
        vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    /** Double-pulse for a habit completion or coin/XP grant. */
    fun reward(context: Context) {
        vibrate(context, longArrayOf(0, 40, 60, 40), intArrayOf(0, 180, 0, 220))
    }

    /** Celebratory pattern used when a focus session completes. */
    fun sessionComplete(context: Context) {
        vibrate(context, longArrayOf(0, 60, 80, 60, 80, 120), intArrayOf(0, 200, 0, 220, 0, 255))
    }

    /** Gentle single buzz used when the user taps "I'm drifting" — acknowledgement, not alarm. */
    fun drift(context: Context) {
        val vibrator = vibrator(context)
        if (!vibrator.hasVibrator()) return
        vibrator.vibrate(VibrationEffect.createOneShot(35, 120))
    }
}
