package com.michaelpohl.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import timber.log.Timber

class NotificationHandler {

    fun buildNotification(context: Context, activityClass: Class<out AppCompatActivity>): Notification {
        Timber.d("Get notification")
        // service intent
        val intent = Intent(context, PlayerService::class.java)
        intent.putExtra(PlayerService.DID_START_FROM_NOTIFICATION, true)
        val servicePendingIntent = PendingIntent.getService(
            context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT,
        )
        // resume activity intent
        val activityPendingIntent = PendingIntent.getActivity(
            context, 0, Intent(context, activityClass), PendingIntent.FLAG_CANCEL_CURRENT
        )

        val builder =
            NotificationCompat.Builder(context, PlayerService.NOTIFICATION_CHANNEL_ID)
                .addAction(0, "launch", activityPendingIntent) // TODO take back in
                .addAction(0, "turn off", servicePendingIntent)
                .setContentTitle("Notification title")
                .setContentText("Notification content)")
                .setOngoing(true)
                .setPriority(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        NotificationManager.IMPORTANCE_LOW
                    } else {
                        Notification.FLAG_ONGOING_EVENT
                    }
                )
                .setSmallIcon(android.R.drawable.sym_def_app_icon) // TODO
                .setWhen(System.currentTimeMillis())

        // if Android O or higher, we need a channel ID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(PlayerService.NOTIFICATION_CHANNEL_ID) // Channel ID
        }
        return builder.build()
    }
}
