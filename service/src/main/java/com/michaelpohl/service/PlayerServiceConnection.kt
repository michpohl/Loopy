package com.michaelpohl.service

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class PlayerServiceConnection(private val activityClass: Class<out AppCompatActivity>) : ServiceConnection {

    private var playerInterface: PlayerService.ServiceBinder? = null

    var onServiceConnectedListener: ((PlayerService.ServiceBinder)-> Unit)? = null

    var playerService: PlayerService? = null
        private set

    fun requestPlayerInterface() : PlayerService.ServiceBinder? {
        return playerInterface
    }

    override fun onServiceConnected(name: ComponentName, service: IBinder) {
        Timber.d("Service connected")
        val binder = service as PlayerService.ServiceBinder
        this@PlayerServiceConnection.playerService = binder.getService().apply {
            activityClass = this@PlayerServiceConnection.activityClass
            start()
        }
        playerInterface = binder
        onServiceConnectedListener?.invoke(binder)
    }

    override fun onServiceDisconnected(name: ComponentName) {
        Timber.d("Service disconnected")
    }
}

