package com.radonshadow.focusdrift.domain.model

import androidx.annotation.RawRes
import com.radonshadow.focusdrift.R

enum class FocusSound(val id: String, val label: String, @RawRes val rawResId: Int?) {
    NONE("none", "Silence", null),
    WHITE_NOISE("white_noise", "White noise", R.raw.sound_white_noise),
    BROWN_NOISE("brown_noise", "Deep hum", R.raw.sound_brown_noise),
    RAIN("rain", "Rain", R.raw.sound_rain),
    CAFE("cafe", "Café", R.raw.sound_cafe);

    companion object {
        fun fromId(id: String): FocusSound = entries.firstOrNull { it.id == id } ?: NONE
    }
}
