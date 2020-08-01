package de.michaelpohl.loopy.model

import android.content.Context
import de.michaelpohl.loopy.common.PlayerState.PAUSED
import de.michaelpohl.loopy.common.PlayerState.PLAYING
import de.michaelpohl.loopy.common.PlayerState.STOPPED
import de.michaelpohl.loopy.common.PlayerState.UNKNOWN
import de.michaelpohl.loopy.common.SwitchingLoopsBehaviour.WAIT
import de.michaelpohl.loopy.common.jni.JniBridge
import de.michaelpohl.loopy.common.jni.JniResult
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

    var hasLoopFile = false
    var state = UNKNOWN
    var isReady = false
        private set

    var waitMode = JniBridge.waitMode
        private set

    lateinit var onLoopSwitchedListener: () -> Unit
    lateinit var onLoopedListener: (Int) -> Unit

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
        Timber.d("Setting WaitMode: $shouldWait")
        val result =  JniBridge.setWaitMode(shouldWait)
        if (result.isSuccess()) waitMode = result.data ?: false
        return result
    }

    fun changePlaybackPosition(newPosition: Float) {
    }

    fun resetPreSelection() {
        //        TODO("Not yet implemented")
    }

    fun setFileStartedByPlayerListener(listener: (String) -> Unit) {
        JniBridge.fileStartedByPlayerListener = listener
    }

    fun setPlaybackProgressListener(listener: (String, Int) -> Unit) {
JniBridge.playbackProgressListener = listener    }

    // always replace the current uri when switching
    // in WAIT mode, we first check if we already have a uri. In that case, we set nextLoopUri for the waiting file
    suspend fun select(path: String): JniResult<String> {
        Timber.d("Selecting")
        return JniBridge.select(path)
    }

    /**
     * This plays the first item from the vector array of our native audio engine.
     * Make sure it's always what we want!!
     */
    private suspend fun startPlayer(): JniResult<String> {
        with(JniBridge.start()) {
            Timber.d("Start Player with: ${this.data}")
            if (this.isSuccess()) state = PLAYING
            return this
        }
    }

    fun preselect(path: String) {
        nextLoopUri = path
    }

    fun getCurrentPosition(): Float {
        return 0F
    }
}
