package com.michaelpohl.loopyplayer2.model

import com.michaelpohl.loopyplayer2.common.PlayerState.*
import com.michaelpohl.loopyplayer2.common.jni.JniBridge
import com.michaelpohl.loopyplayer2.common.jni.JniResult
import org.koin.core.KoinComponent

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

    suspend fun resume(): JniResult<Nothing> {
        with(JniBridge.resume()) {
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
        // TODO reimplement
        return 0F
    }

    suspend fun setSampleRate(sampleRate: Int): JniResult<Int> {
       return JniBridge.setSampleRate(sampleRate)
    }
}
