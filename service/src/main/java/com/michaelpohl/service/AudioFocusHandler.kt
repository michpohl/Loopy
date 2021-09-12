package com.michaelpohl.service

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import timber.log.Timber

class AudioFocusHandler(
    private val onFocusGained: () -> Unit,
    private val onFocusLost: () -> Unit,
    private val onFocusLostTransient: () -> Unit = onFocusLost) : AudioManager.OnAudioFocusChangeListener {

    var authorization = PlaybackAuthorization.NOT_GRANTED
    fun requestAudioFocus(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val focusLock = Any()

        val requestResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.requestAudioFocus(buildFocusRequest(this))
        } else {
            audioManager.requestAudioFocus(this, AudioAttributes.CONTENT_TYPE_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }

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

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                Timber.d("Audio focus Gain")
                onFocusGained()
            }

            AudioManager.AUDIOFOCUS_LOSS -> {
                Timber.d("Audio focus loss")
                onFocusLost()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                Timber.d("Audio focus loss transient")
                onFocusLostTransient()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                Timber.d("Audio focus loss transient, can duck")
                onFocusLostTransient()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun buildFocusRequest(focusChangeListener: AudioManager.OnAudioFocusChangeListener): AudioFocusRequest {
        return AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
            setAudioAttributes(AudioAttributes.Builder().run {
                setUsage(AudioAttributes.USAGE_MEDIA)
                setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                build()
            })
            setAcceptsDelayedFocusGain(true) // TODO check if that works properly with our content
            setOnAudioFocusChangeListener(focusChangeListener)
            build()
        }
    }

    enum class PlaybackAuthorization {
        GRANTED, NOT_GRANTED, DELAYED
    }
}
