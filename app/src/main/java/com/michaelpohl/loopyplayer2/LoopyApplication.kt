package com.michaelpohl.loopyplayer2

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class LoopyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        setupTimber()
        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@LoopyApplication)
            modules(KoinGraph.get())
        }
    }

    private fun setupTimber() {
        if (com.michaelpohl.loopyplayer2.BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
