package de.michaelpohl.loopy.model

import de.michaelpohl.loopy.common.PlayerState.PAUSED
import de.michaelpohl.loopy.common.PlayerState.PLAYING
import de.michaelpohl.loopy.common.PlayerState.STOPPED
import de.michaelpohl.loopy.common.PlayerState.UNKNOWN
import de.michaelpohl.loopy.common.jni.JniBridge
import de.michaelpohl.loopy.common.jni.JniResult
import org.koin.core.KoinComponent
import timber.log.Timber

class JniPlayer : KoinComponent {

    var hasLoopFile = false
    var state = UNKNOWN
    var isReady = false
        private set

    var waitMode = JniBridge.waitMode
        private set

    lateinit var onLoopedListener: (Int) -> Unit

    suspend fun start(): JniResult<String> {
        with(JniBridge.start()) {
            Timber.d("Trying to start playback: ${this.isSuccess()}")
            if (this.isSuccess()) state = PLAYING
            return@start this
        }
    }

    suspend fun pause(): JniResult<Nothing> {
        with(JniBridge.pause()) {
            Timber.d("Trying to pause playback: ${this.isSuccess()}")
            if (this.isSuccess()) state = PAUSED
            return@pause this
        }
    }

    suspend fun resume(): JniResult<Nothing> {
        with(JniBridge.resume()) {
            Timber.d("Trying to resume playback: ${this.isSuccess()}")
            if (this.isSuccess()) state = PLAYING
            return@resume this
        }
    }

    suspend fun stop(): JniResult<Nothing> {
        with(JniBridge.stop()) {
            if (this.isSuccess()) state = STOPPED
            return@stop this
        }
    }

    suspend fun setWaitMode(shouldWait: Boolean): JniResult<Boolean> {
        val result = JniBridge.setWaitMode(shouldWait)
        if (result.isSuccess()) waitMode = result.data ?: false
        return result
    }

    fun changePlaybackPosition(newPosition: Float) {
        //        TODO("Not yet implemented")
    }

    fun resetPreSelection() {
        //        TODO("Not yet implemented")
    }

    fun setFileStartedByPlayerListener(listener: (String) -> Unit) {
        JniBridge.fileStartedByPlayerListener = listener
    }

    fun setPlaybackProgressListener(listener: (String, Int) -> Unit) {
        JniBridge.playbackProgressListener = listener
    }

    suspend fun select(path: String): JniResult<String> {
        return JniBridge.select(path)
    }

    fun getCurrentPosition(): Float {
        return 0F
    }
}
