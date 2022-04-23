package com.michaelpohl.service

import android.content.Intent
import android.media.session.MediaSession
import android.view.KeyEvent

class SessionCallback(
    val onPlayOrPausePressed: () -> Unit,
) : MediaSession.Callback() {

    override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
        val keyEvent = mediaButtonIntent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
        if (keyEvent?.keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
            onPlayOrPausePressed()
        }
        return true
    }
}
