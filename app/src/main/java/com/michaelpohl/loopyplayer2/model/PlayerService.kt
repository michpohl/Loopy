package com.michaelpohl.loopyplayer2.model

import android.app.Service
import android.content.Intent
import android.os.IBinder
import timber.log.Timber

class PlayerService : Service() {

    override fun onCreate() {
        super.onCreate()
        Timber.d("The service exists!")
    }

    private val playerServiceBinder = PlayerServiceBinder()
    override fun onBind(intent: Intent): IBinder? {
        return playerServiceBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }
}
