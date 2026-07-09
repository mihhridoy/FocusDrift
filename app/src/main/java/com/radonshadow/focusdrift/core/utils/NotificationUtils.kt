package com.radonshadow.focusdrift.core.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.radonshadow.focusdrift.R
import com.radonshadow.focusdrift.core.extensions.toMMSS
import com.radonshadow.focusdrift.ui.MainActivity

object NotificationUtils {

    const val CHANNEL_TIMER = "focusdrift_timer"
    const val CHANNEL_HABIT = "focusdrift_habit"
    const val CHANNEL_ENGAGEMENT = "focusdrift_engagement"

    fun ensureChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_TIMER,
                context.getString(R.string.notification_channel_timer),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.notification_channel_timer_desc)
                setShowBadge(false)
            }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_HABIT,
                context.getString(R.string.notification_channel_habit),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_channel_habit_desc)
            }
        )

        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ENGAGEMENT,
                "Focus reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily focus check-ins and streak alerts"
            }
        )
    }

    private fun contentIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun timerNotification(
        context: Context,
        title: String,
        remainingMs: Long
    ) = NotificationCompat.Builder(context, CHANNEL_TIMER)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(title)
        .setContentText(remainingMs.toMMSS() + " remaining")
        .setOngoing(true)
        .setOnlyAlertOnce(true)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setContentIntent(contentIntent(context))
        .build()

    /**
     * Habit reminders escalate through three rounds a day (reminder time, +3h, +6h) with
     * different copy each time, so the repeat nudges read as intentional instead of the same
     * notification glitching in three times.
     */
    fun habitReminderNotification(context: Context, habitName: String, round: Int = 0): android.app.Notification {
        val (title, text) = when (round) {
            0 -> "Time for \"$habitName\"" to "A quick check-in now keeps your streak alive."
            1 -> "\"$habitName\" is still waiting" to "No pressure — but today isn't over yet. You've got this."
            else -> "Last call for \"$habitName\"" to "Your streak resets at midnight. One tap to keep it going."
        }
        return NotificationCompat.Builder(context, CHANNEL_HABIT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent(context))
            .build()
    }

    /** Daily focus check-ins (morning kickoff, midday nudge, evening streak warning). */
    fun engagementNotification(context: Context, title: String, text: String): android.app.Notification =
        NotificationCompat.Builder(context, CHANNEL_ENGAGEMENT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(contentIntent(context))
            .build()
}
