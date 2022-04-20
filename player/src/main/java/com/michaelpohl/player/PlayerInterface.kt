package com.michaelpohl.player

import com.michaelpohl.shared.JniResult
import com.michaelpohl.shared.PlayerState

// TODO evaluate if needed
@Suppress("TooManyFunctions") // Todo remove some, there are redundancies!
interface PlayerInterface {

    suspend fun select(path: String): JniResult<String>
    suspend fun play(): JniResult<String>
    suspend fun pause(): JniResult<Nothing>
    suspend fun resume(): JniResult<Nothing>
    suspend fun stop(): JniResult<Nothing>
    suspend fun setWaitMode(shouldWait: Boolean): JniResult<Boolean>
    suspend fun setSampleRate(sampleRate: Int): JniResult<Int>
    fun setFileStartedByPlayerListener(listener: (String) -> Unit)
    fun setPlaybackProgressListener(listener: (String, Int) -> Unit)
    fun getCurrentPosition(): Float
    fun changePlaybackPosition(newPosition: Float)
    fun resetPreSelection()
    fun isReady(): Boolean
    fun getState(): PlayerState //TODO reduce the state calls to this one
    fun getWaitMode(): Boolean
    fun hasLoopFile(): Boolean
    fun setHasLoopFile(hasFile: Boolean)
    fun setOnLoopedListener(receiver: (Int) -> Unit)
}
