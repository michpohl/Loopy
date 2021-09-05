package com.michaelpohl.service

import android.app.*
import android.content.Intent
import android.media.session.MediaSession
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import com.michaelpohl.shared.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class PlayerService : Service() {

    private val sessionCallback = SessionCallback()
    private val playerServiceBinder = ServiceBinder()
    private val notificationHandler = NotificationHandler()
    private val focusHandler = AudioFocusHandler({onAudioFocusGained()}, {onAudioFocusLost()})
    private lateinit var session: MediaSession

    private lateinit var notificationManager: NotificationManager
    private lateinit var serviceHandler: Handler

    var activityClass: Class<out AppCompatActivity>? = null
    var serviceState = ServiceState.STOPPED
    private fun startAudioFocus() {
        focusHandler.requestAudioFocus(this)
    }

    private fun onAudioFocusGained() {
        if (playerServiceBinder.getState() == PlayerState.PAUSED) {
            CoroutineScope(Dispatchers.Default).launch {
                playerServiceBinder.play() // play if it was paused
            }
        }
    }

    private fun onAudioFocusLost() {
        CoroutineScope(Dispatchers.Default).launch {
            with(playerServiceBinder) {
                if (this.getState() == PlayerState.PLAYING) {
                    this.pause() // pause player
                } else {
                    stop() // stop service
                }
            }
        }
    }

    fun start() {
        if (serviceState != ServiceState.RUNNING) startService(Intent(applicationContext, PlayerService::class.java))
        serviceState = ServiceState.RUNNING
        startAudioFocus()
    }

    override fun onCreate() {
        Timber.d("Service created")
        super.onCreate()
        createMediaSession()

        setupThread()
        setupNotification()
    }

    private fun createMediaSession() {
        session = MediaSession(this, TAG).apply {
            setCallback(sessionCallback) // TODO maybe separate later into a separate session class
        }
        session.isActive = true
        playerServiceBinder.session = session
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("Service started")
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

        // if we're not playing, no need to go foreground
        Timber.d("State: ${playerServiceBinder.getState()}")

        // if we don't know the activity class yet, we can't properly set up the service and notification
        // and therefore we shouldn't do anything
        activityClass?.let {
            if (playerServiceBinder.getState() != PlayerState.PLAYING) return true
            startForeground(NOTIFICATION_ID, notificationHandler.buildNotification(this, activityClass!!))
            return true
        } ?: Timber.w("No activity class found!")
        return false
    }

    private fun stop() {
        Timber.d("Service stopped")
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
        // TODO check which places these should go to
        private val TAG = PlayerService::class.java.simpleName
        const val NOTIFICATION_CHANNEL_ID = "loopy_channel"
        private const val NOTIFICATION_ID = 56479
        const val DID_START_FROM_NOTIFICATION = "started_from_notification"
    }
}

enum class ServiceState {
    RUNNING, STOPPED
}
