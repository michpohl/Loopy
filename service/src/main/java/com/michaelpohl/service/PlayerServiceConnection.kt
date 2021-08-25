package com.michaelpohl.service

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class PlayerServiceConnection(private val activityClass: Class<out AppCompatActivity>) : ServiceConnection {

    var onServiceConnectedListener: ((PlayerService.ServiceBinder)-> Unit)? = null

    var service: PlayerService? = null
        private set



    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        Timber.d("Service connected")
        val binder = service as PlayerService.ServiceBinder
        this@PlayerServiceConnection.service = binder.service.apply {
            activityClass = this@PlayerServiceConnection.activityClass
            start()
        }
        onServiceConnectedListener?.invoke(binder)
    }

    override fun onServiceDisconnected(name: ComponentName) {
        Timber.d("Service disconnected")
    }
}

