package com.radonshadow.focusdrift.core.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import com.radonshadow.focusdrift.domain.model.FocusSound
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Loops one of the bundled ambient beds (res/raw/sound_*.wav, all originally synthesized so the
 * app ships with no licensed audio) behind an active focus session. Owned by [FocusTimerService]
 * so playback follows the same lifecycle as the countdown itself.
 */
@Singleton
class FocusSoundPlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var player: MediaPlayer? = null
    private var currentSound: FocusSound = FocusSound.NONE
    private var currentVolume: Float = 0.5f

    fun start(sound: FocusSound, volume: Float) {
        stop()
        val resId = sound.rawResId ?: return
        currentSound = sound
        currentVolume = volume
        runCatching {
            val afd = context.resources.openRawResourceFd(resId)
            player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                afd.close()
                isLooping = true
                setVolume(volume, volume)
                prepare()
                start()
            }
        }
    }

    fun pause() {
        runCatching { player?.takeIf { it.isPlaying }?.pause() }
    }

    fun resume() {
        runCatching { player?.start() }
    }

    fun setVolume(volume: Float) {
        currentVolume = volume.coerceIn(0f, 1f)
        runCatching { player?.setVolume(currentVolume, currentVolume) }
    }

    fun stop() {
        runCatching {
            player?.stop()
            player?.release()
        }
        player = null
        currentSound = FocusSound.NONE
    }
}
