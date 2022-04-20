package com.michaelpohl.service

import android.content.Intent
import android.media.session.MediaSession
import timber.log.Timber

class SessionCallback(
    val doOnPlay: () -> Unit,
    val doOnPause: () -> Unit,
    val doOnStop: () -> Unit,
) : MediaSession.Callback() {

    override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
        Timber.d("There was an event")
        return super.onMediaButtonEvent(mediaButtonIntent)
    }
}
