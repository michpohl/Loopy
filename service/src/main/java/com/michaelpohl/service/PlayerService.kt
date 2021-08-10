package com.michaelpohl.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.session.MediaSession
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import timber.log.Timber
import java.util.*

class PlayerService : Service(), AudioManager.OnAudioFocusChangeListener {

    private val sessionCallback = SessionCallback()
    private val playerServiceBinder = ServiceBinder()
    private lateinit var session: MediaSession

    private lateinit var notificationManager: NotificationManager
    private lateinit var serviceHandler: Handler

    var activityClass: Class<out AppCompatActivity>? = null
    var serviceState = ServiceState.STOPPED

//    // initializing variables for audio focus and playback management
//    val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
//    val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
//        setAudioAttributes(AudioAttributes.Builder().run {
//            setUsage(AudioAttributes.USAGE_GAME)
//            setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//            build()
//        })
//        setAcceptsDelayedFocusGain(true)
//        setOnAudioFocusChangeListener { Timber.d("onAudioFocusChanged") }
//        build()
//    }
//    val focusLock = Any()
//
//    var playbackDelayed = false
//    var playbackNowAuthorized = false
//
//    // requesting audio focus and processing the response
//    val res = audioManager.requestAudioFocus(focusRequest)
//
//    init {
//
//        synchronized(focusLock) {
//            playbackNowAuthorized = when (res) {
//                AudioManager.AUDIOFOCUS_REQUEST_FAILED -> false
//                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> {
//                    // play
//                    true
//                }
//                AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> {
//                    playbackDelayed = true
//                    false
//                }
//                else -> false
//            }
//        }
//    }
//
//    // implementing OnAudioFocusChangeListener to react to focus changes
    override fun onAudioFocusChange(focusChange: Int) {
//        when (focusChange) {
//            AudioManager.AUDIOFOCUS_GAIN -> Timber.d("Focus Gain")
////                if (playbackDelayed || resumeOnFocusGain) {
////                    synchronized(focusLock) {
////                        playbackDelayed = false
////                        resumeOnFocusGain = false
////                    }
////                    playbackNow()
////                }
//            AudioManager.AUDIOFOCUS_LOSS -> { Timber.d("Focus loss")
////                synchronized(focusLock) {
////                    resumeOnFocusGain = false
////                    playbackDelayed = false
////                }
////                pausePlayback()
//            }
//            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> { Timber.d("Focus Loss Transient")
////                synchronized(focusLock) {
////                    // only resume if playback is being interrupted
////                    resumeOnFocusGain = isPlaying()
////                    playbackDelayed = false
////                }
////                pausePlayback()
//            }
//            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
//                // ... pausing or ducking depends on your app
//            }
//        }
    }

    fun start() {
        if (serviceState != ServiceState.RUNNING) startService(Intent(applicationContext, PlayerService::class.java))
        serviceState = ServiceState.RUNNING
        session = MediaSession(this, TAG).apply {
            setCallback(sessionCallback) // TODO maybe separate later into a separate session class
        }
        session.isActive = true
        playerServiceBinder.session = session
    }

    override fun onCreate() {
        Timber.d("Service created")
        super.onCreate()

        setupThread()
        setupNotification()
    }

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
