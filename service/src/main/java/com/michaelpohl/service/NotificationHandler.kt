package com.michaelpohl.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import timber.log.Timber
import com.michaelpohl.shared.R as shared

class NotificationHandler {

    @SuppressLint("UnspecifiedImmutableFlag")
    fun buildNotification(context: Context, activityClass: Class<out AppCompatActivity>): Notification {
        Timber.d("Get notification")
        // service intent
        val intent = Intent(context.applicationContext, PlayerService::class.java)
        intent.putExtra(PlayerService.DID_START_FROM_NOTIFICATION, true)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val servicePendingIntent = PendingIntent.getService(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        // resume activity intent
        val activityPendingIntent = PendingIntent.getActivity(
            context, 0, Intent(context, activityClass), PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder =
            NotificationCompat.Builder(context, PlayerService.NOTIFICATION_CHANNEL_ID)
                .addAction(0, context.getString(shared.string.notification_btn_open_app), activityPendingIntent)
                .addAction(0, context.getString(shared.string.notification_btn_stop_player), servicePendingIntent)
                .setContentTitle(context.getString(shared.string.notification_title))
                .setOngoing(true)
                .setPriority(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        NotificationManager.IMPORTANCE_LOW
                    } else {
                        Notification.FLAG_ONGOING_EVENT
                    }
                )
                .setSmallIcon(shared.drawable.ic_service_logo)
                .setWhen(System.currentTimeMillis())

        // if Android O or higher, we need a channel ID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(PlayerService.NOTIFICATION_CHANNEL_ID) // Channel ID
        }
        return builder.build()
    }
}
