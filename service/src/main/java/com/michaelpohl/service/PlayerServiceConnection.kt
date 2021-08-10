package com.michaelpohl.service

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

// TODO can the activityClass be injected? Should it?
class PlayerServiceConnection(private val activityClass: Class<out AppCompatActivity>) : ServiceConnection {

    var service: PlayerService? = null
        private set

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        Timber.d("Service connected")
        val binder = service as PlayerService.ServiceBinder
        this@PlayerServiceConnection.service = binder.service.apply {
            activityClass = this@PlayerServiceConnection.activityClass
            start()
        }
    }

    override fun onServiceDisconnected(name: ComponentName) {
        Timber.d("Service disconnected")
    }
}

