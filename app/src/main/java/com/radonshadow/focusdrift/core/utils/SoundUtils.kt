package com.radonshadow.focusdrift.core.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager

/**
 * Session-complete/break chimes ship as system notification tones rather than bundled mp3s so the
 * app has no unlicensed audio assets baked in. Swap [playSessionComplete]/[playBreakStart] for a
 * bundled R.raw sound once the product has licensed chime audio.
 */
object SoundUtils {

    fun playSessionComplete(context: Context) = playSystemTone(context, RingtoneManager.TYPE_NOTIFICATION)

    fun playBreakStart(context: Context) = playSystemTone(context, RingtoneManager.TYPE_NOTIFICATION)

    private fun playSystemTone(context: Context, type: Int) {
        val uri = RingtoneManager.getActualDefaultRingtoneUri(context, type) ?: return
        runCatching {
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                setDataSource(context, uri)
                setOnCompletionListener { it.release() }
                prepare()
                start()
            }
            player.setOnErrorListener { mp, _, _ -> mp.release(); true }
        }
    }
}
