package com.michaelpohl.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import timber.log.Timber

class AudioFocusHandler {

    var authorization = PlaybackAuthorization.NOT_GRANTED

    var playbackDelayed = false
    var playbackNowAuthorized = false
    fun requestaudioFocus(context: Context, focusChangeListener: AudioManager.OnAudioFocusChangeListener) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
            setAudioAttributes(AudioAttributes.Builder().run {
                setUsage(AudioAttributes.USAGE_GAME)
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                build()
            })
            setAcceptsDelayedFocusGain(true)
            setOnAudioFocusChangeListener(focusChangeListener)
            build()
        }
        val focusLock = Any()

        val requestResult = audioManager.requestAudioFocus(focusRequest)
        Timber.d("AudioFocusRequest result: $requestResult")
        synchronized(focusLock) {
            authorization = when (requestResult) {
                AudioManager.AUDIOFOCUS_REQUEST_FAILED -> PlaybackAuthorization.NOT_GRANTED
                AudioManager.AUDIOFOCUS_REQUEST_GRANTED -> PlaybackAuthorization.GRANTED
                AudioManager.AUDIOFOCUS_REQUEST_DELAYED -> PlaybackAuthorization.DELAYED

                else -> PlaybackAuthorization.NOT_GRANTED
            }
        }
    }

    enum class PlaybackAuthorization {
        GRANTED, NOT_GRANTED, DELAYED
    }
}
