package com.michaelpohl.service

import android.app.*
import android.content.Intent
import android.media.session.MediaSession
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import timber.log.Timber

class PlayerService : Service() {

    private val sessionCallback = SessionCallback()
    private lateinit var session : MediaSession

    private lateinit var notificationManager: NotificationManager
    private lateinit var serviceHandler: Handler

    var activityClass: Class<out AppCompatActivity>? = null
    var serviceState = ServiceState.STOPPED

    fun start() {
        if (serviceState != ServiceState.RUNNING) startService(Intent(applicationContext, PlayerService::class.java))
        serviceState = ServiceState.RUNNING
        session = MediaSession(this.applicationContext, TAG).apply {
            setCallback(sessionCallback) // TODO maybe separate later
        }
        session.isActive = true
    }

    override fun onCreate() {
        Timber.d("Service created")
        super.onCreate()

        setupThread()
        setupNotification()
    }

    private val playerServiceBinder = ServiceBinder()
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("service started")
        return START_NOT_STICKY
    }

    private fun setupNotification() {
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "here be app name", // TODO
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                notificationManager.createNotificationChannel(this)
            }
        }
    }

    private fun getNotification(): Notification {
        Timber.d("Get notification")
        // service intent
        val intent = Intent(this, PlayerService::class.java)
        intent.putExtra(DID_START_FROM_NOTIFICATION, true)
        val servicePendingIntent = PendingIntent.getService(
            this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        // resume activity intent
        val activityPendingIntent = PendingIntent.getActivity(
            this, 0, Intent(this, activityClass!!), 0
        )

        val builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .addAction(0, "launch", activityPendingIntent) // TODO take back in
                .addAction(0, "turn off", servicePendingIntent)
                .setContentTitle("Notification title")
                .setContentText("Notification content)")
                .setOngoing(true)
                .setPriority(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        NotificationManager.IMPORTANCE_HIGH
                    } else {
                        Notification.FLAG_HIGH_PRIORITY
                    }
                )
                .setSmallIcon(android.R.drawable.sym_def_app_icon) // TODO
                .setWhen(System.currentTimeMillis())

        // if Android O or higher, we need a channel ID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(NOTIFICATION_CHANNEL_ID) // Channel ID
        }
        return builder.build()
    }

    private fun setupThread() {
        val handlerThread = HandlerThread(TAG)
        handlerThread.start()
        serviceHandler = Handler(handlerThread.looper)
    }

    override fun onBind(intent: Intent): IBinder {
        stopForeground(true)
        return playerServiceBinder
    }

    override fun onRebind(intent: Intent) {
        stopForeground(true)
        super.onRebind(intent)
    }

    // activity is gone or backgrounded, so we want to go foreground
    override fun onUnbind(intent: Intent): Boolean {
        Timber.d("onUnbind")

        // if we don't know the activity class yet, we can't properly set up the service and notification
        // and therefore we shouldn't do anything
        activityClass?.let {
            startForeground(NOTIFICATION_ID, getNotification())
            return true
        } ?: Timber.w("No activity class found!")
        return false
    }

    private fun stop() {
        serviceState = ServiceState.STOPPED
        stopSelf()
    }

    override fun onDestroy() {
        serviceHandler.removeCallbacksAndMessages(null)
        session.isActive = false
        super.onDestroy()
    }

    // if the user wants to end the service from the notification, this gets executed
    private fun handleNotificationStopClicked(intent: Intent?) {
        with(
            intent?.getBooleanExtra(
                DID_START_FROM_NOTIFICATION,
                false
            )
        ) {
            if (this == true) {
                // do whatever else might be necessary
                stop()
            }
        }
    }
    // TODO let's see if this is the smartest way...
    inner class ServiceBinder : PlayerServiceBinder() {
        val service: PlayerService // used to be internal but I wasn't allowed to do this
            get() = this@PlayerService
    }

    companion object {

        private val TAG = PlayerService::class.java.simpleName
        private const val NOTIFICATION_CHANNEL_ID = "loopy_channel"
        private const val NOTIFICATION_ID = 56479
        private const val DID_START_FROM_NOTIFICATION = "started_from_notification"
    }
}

enum class ServiceState {
    RUNNING, STOPPED
}
