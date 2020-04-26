package de.michaelpohl.loopy.model

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import de.michaelpohl.loopy.common.PlayerState
import de.michaelpohl.loopy.common.jni.JniBridge

class JniPlayer() {

    private var mContext: Context? = null
    private var mCounter = 1
    private var shouldBePlaying = false
    private var loopsElapsed = 0

    lateinit var currentPlayer: MediaPlayer
    private lateinit var nextPlayer: MediaPlayer
    private lateinit var loopUri: Uri

    var switchingLoopsBehaviour = DataRepository.settings.switchingLoopsBehaviour
    var hasLoopFile = false
    var state = PlayerState.UNKNOWN
    var isReady = false
        private set

    lateinit var onLoopSwitchedListener: () -> Unit
    lateinit var onLoopedListener: (Int) -> Unit
    fun start() {
        JniBridge.stop()
        JniBridge.load(loopUri.toString())
        JniBridge.play()
    }

    fun pause() {
        TODO("Not yet implemented")
    }

    fun stop() {
        JniBridge.stop()
    }

    fun changePlaybackPosition(newPosition: Float) {
    }

    fun resetPreSelection() {
        TODO("Not yet implemented")
    }

    fun isPlaying(): Boolean {
        return false
    }

    fun setLoopUri(uri: Uri) {
        this.loopUri = uri
    }

    fun getCurrentPosition(): Float {
        return 0F
    }
}