package com.michaelpohl.player

import com.michaelpohl.loopyplayer2.common.PlayerState.*
import com.michaelpohl.shared.JniResult
import org.koin.core.KoinComponent

class JniPlayer : KoinComponent {

    var hasLoopFile = false
    var state = UNKNOWN
    var isReady = false
        private set

    var waitMode = com.michaelpohl.player.jni.JniBridge.waitMode
        private set

    lateinit var onLoopedListener: (Int) -> Unit
    suspend fun start(): JniResult<String> {
        with(com.michaelpohl.player.jni.JniBridge.start()) {
            if (this.isSuccess()) state = PLAYING
            return@start this
        }
    }

    suspend fun pause(): JniResult<Nothing> {
        with(com.michaelpohl.player.jni.JniBridge.pause()) {
            if (this.isSuccess()) state = PAUSED
            return@pause this
        }
    }

    suspend fun resume(): JniResult<Nothing> {
        with(com.michaelpohl.player.jni.JniBridge.resume()) {
            if (this.isSuccess()) state = PLAYING
            return@resume this
        }
    }

    suspend fun stop(): JniResult<Nothing> {
        with(com.michaelpohl.player.jni.JniBridge.stop()) {
            if (this.isSuccess()) state = STOPPED
            return@stop this
        }
    }

    suspend fun setWaitMode(shouldWait: Boolean): JniResult<Boolean> {
        val result = com.michaelpohl.player.jni.JniBridge.setWaitMode(shouldWait)
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
        com.michaelpohl.player.jni.JniBridge.fileStartedByPlayerListener = listener
    }

    fun setPlaybackProgressListener(listener: (String, Int) -> Unit) {
        com.michaelpohl.player.jni.JniBridge.playbackProgressListener = listener
    }

    suspend fun select(path: String): JniResult<String> {
        return com.michaelpohl.player.jni.JniBridge.select(path)
    }

    fun getCurrentPosition(): Float {
        // TODO reimplement
        return 0F
    }

    suspend fun setSampleRate(sampleRate: Int): JniResult<Int> {
       return com.michaelpohl.player.jni.JniBridge.setSampleRate(sampleRate)
    }
}
