package de.michaelpohl.loopy.model

import android.app.Service
import android.content.Intent
import android.os.IBinder
import timber.log.Timber

class PlayerService : Service() {

    override fun onCreate() {
        super.onCreate()
        Timber.d("The service exists!")
    }

    private val playerServiceBinder = PlayerServiceBinder(this)

    override fun onBind(intent: Intent): IBinder? {
        return playerServiceBinder
    }
}
