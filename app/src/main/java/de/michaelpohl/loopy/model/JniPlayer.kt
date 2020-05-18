package de.michaelpohl.loopy.model

import android.content.Context
import android.media.MediaPlayer
import de.michaelpohl.loopy.common.PlayerState.PAUSED
import de.michaelpohl.loopy.common.PlayerState.PLAYING
import de.michaelpohl.loopy.common.PlayerState.STOPPED
import de.michaelpohl.loopy.common.PlayerState.UNKNOWN
import de.michaelpohl.loopy.common.SwitchingLoopsBehaviour.WAIT
import de.michaelpohl.loopy.common.jni.JniBridge
import org.koin.core.KoinComponent

class JniPlayer() : KoinComponent {


    private var mContext: Context? = null
    private var mCounter = 1
    private var shouldBePlaying = false
    private var loopsElapsed = 0

    // TODO remove / improve when it all works
    var switchingLoopsBehaviour = WAIT

    lateinit var currentPlayer: MediaPlayer
    private lateinit var nextPlayer: MediaPlayer
    private var loopLocation: String? = null
    private var nextLoopUri: String? = null

    //    var switchingLoopsBehaviour = DataRepository.settings.switchingLoopsBehaviour
    var hasLoopFile = false
    var state = UNKNOWN
    var isReady = false
        private set

    lateinit var onLoopSwitchedListener: () -> Unit
    lateinit var onLoopedListener: (Int) -> Unit

    // TODO refactor this into prepare & start, and change isReady to be a PlayerState too
    fun start() {
        JniBridge.play()
        state = PLAYING
    }

    fun pause() {
        if (JniBridge.pause()) state = PAUSED
    }

    fun stop() {
        JniBridge.stop()
        state = STOPPED
    }

    fun changePlaybackPosition(newPosition: Float) {
    }

    fun resetPreSelection() {
        TODO("Not yet implemented")
    }

    fun isPlaying(): Boolean {
        return state == PLAYING
    }

    // always replace the current uri when switching
    // in WAIT mode, we first check if we already have a uri. In that case, we set nextLoopUri for the waiting file
    fun prepare(path: String) {
        loopLocation = path
        if (state == PAUSED) {
            JniBridge.play()
            return
        }
        JniBridge.load(loopLocation.toString(), true)
        isReady = true
    }

    fun preselect(path: String) {
        nextLoopUri = path
    }

    fun getCurrentPosition(): Float {
        return 0F
    }
}