package de.michaelpohl.loopy.model

import android.content.Context
import de.michaelpohl.loopy.common.PlayerState.PAUSED
import de.michaelpohl.loopy.common.PlayerState.PLAYING
import de.michaelpohl.loopy.common.PlayerState.STOPPED
import de.michaelpohl.loopy.common.PlayerState.UNKNOWN
import de.michaelpohl.loopy.common.SwitchingLoopsBehaviour.WAIT
import de.michaelpohl.loopy.common.jni.JniBridge
import de.michaelpohl.loopy.common.jni.JniResult
import de.michaelpohl.loopy.common.jni.errorResult
import de.michaelpohl.loopy.common.jni.toJniResult
import org.koin.core.KoinComponent
import timber.log.Timber

class JniPlayer : KoinComponent {

    private var mContext: Context? = null
    private var mCounter = 1
    private var shouldBePlaying = false
    private var loopsElapsed = 0

    // TODO remove / improve when it all works
    var switchingLoopsBehaviour = WAIT

    private var loopLocation: String? = null
    private var nextLoopUri: String? = null

    //    var switchingLoopsBehaviour = DataRepository.settings.switchingLoopsBehaviour
    var hasLoopFile = false
    var state = UNKNOWN
    var isReady = false
        private set

    lateinit var onLoopSwitchedListener: () -> Unit
    lateinit var onLoopedListener: (Int) -> Unit

    //    runs start if we have a filename, if the bridge knows a selected file, otherwise it returns an error
    suspend fun start(): JniResult<String> {
        with(JniBridge.start()) {
            if (this.isSuccess()) state = PLAYING
            return@start this
        }
    }

    suspend fun pause(): JniResult<Nothing> {
        with(JniBridge.pause()) {
            if (this.isSuccess()) state = PAUSED
            return@pause this
        }
    }

    suspend fun stop(): JniResult<Nothing> {
        with(JniBridge.stop()) {
            if (this.isSuccess()) state = STOPPED
            return@stop this
        }
    }

    suspend fun setWaitMode(shouldWait: Boolean): JniResult<Boolean> {
        return JniBridge.setWaitMode(shouldWait)
    }

    fun changePlaybackPosition(newPosition: Float) {
    }

    fun resetPreSelection() {
//        TODO("Not yet implemented")
    }

    fun isPlaying(): Boolean {
        return state == PLAYING
    }

    // always replace the current uri when switching
    // in WAIT mode, we first check if we already have a uri. In that case, we set nextLoopUri for the waiting file
    suspend fun select(path: String): JniResult<Nothing> {
        Timber.d("Selecting")
        with(JniBridge.select(path).isSuccess()) {
            Timber.d("Select success: $this")
            if (this) {
                isReady = true // TODO let's see if this still makes sense
                // TODO here we could turn on and of autoplay
                startPlayerIfNotPlaying()
            }
            return@select this.toJniResult()
        }
    }

    /**
     * This plays the first item from the vector array of our native audio engine.
     * Make sure it's always what we want!!
     */
    private suspend fun startPlayerIfNotPlaying(): JniResult<String> {
        Timber.d("Start if not playing")
        return if (!isPlaying()) {
            JniBridge.start()
        } else {
            errorResult()
        }
    }

    fun preselect(path: String) {
        nextLoopUri = path
    }

    fun getCurrentPosition(): Float {
        return 0F
    }
}
