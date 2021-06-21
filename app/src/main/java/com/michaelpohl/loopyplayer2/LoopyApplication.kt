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

class LoopyApplication : Application() {

    override fun attachBaseContext(base:Context) {
        super.attachBaseContext(base)

        initAcra {
            //core configuration:
            buildConfigClass = BuildConfig::class.java
            reportFormat = StringFormat.JSON
            //each plugin you chose above can be configured in a block like this:
            dialog {
                //required
                text = "hi"
                //optional, enables the dialog title
                title = "title"
                //defaults to android.R.string.ok
                positiveButtonText = "positive"
                //defaults to android.R.string.cancel
                negativeButtonText = "negative"
                //optional, enables the comment input
                commentPrompt = "commentprompt"
                //optional, enables the email input
                emailPrompt = "emailprompt"
                //defaults to android.R.drawable.ic_dialog_alert
//                resIcon = R.drawable.ic_menu_share
                //optional, defaults to @android:style/Theme.Dialog
                resTheme = R.style.AppTheme
                //allows other customization
//                reportDialogClass = MyCustomDialog::class.java
            }
            mailSender {
                //required
                mailTo = "google@michaelpohl.de"
                //defaults to true
                reportAsFile = true
                //defaults to ACRA-report.stacktrace
                reportFileName = "Crash.txt"
                //defaults to "<applicationId> Crash Report"
                subject = "subject"
                //defaults to empty
                body = "body"
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
