package com.michaelpohl.loopyplayer2

import android.app.Application
import android.content.Context
import org.acra.config.dialog
import org.acra.config.mailSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber
import java.util.*

class LoopyApplication : Application() {

    override fun attachBaseContext(base:Context) {
        super.attachBaseContext(base)

        initAcra {
            //core configuration:
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON

            // plugins
            dialog {
                text = getString(R.string.acra_dialog_text)
                positiveButtonText = getString(R.string.yes)
                negativeButtonText = getString(R.string.no)
                resTheme = R.style.AppTheme
            }
            mailSender {
                mailTo = getString(R.string.error_reporting_email)
                reportAsFile = true
                reportFileName = "${Date()}_${BuildConfig.VERSION_NAME}_CrashReport.txt"
                subject = getString(R.string.error_reporting_subject)
            }
        }
    }

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
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
