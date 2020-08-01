package de.michaelpohl.loopy.model

import de.michaelpohl.loopy.common.PlayerState
import de.michaelpohl.loopy.common.SwitchingLoopsBehaviour
import de.michaelpohl.loopy.common.jni.JniResult

interface PlayerServiceInterface {
    suspend fun preselect(path: String) : JniResult<String> // TODO this should be obsolete if I'm doing it right
    suspend fun select(path: String) : JniResult<String>
    suspend fun play(): JniResult<String>
    suspend fun pause() : JniResult<Nothing>
    suspend fun stop() : JniResult<Nothing>
    suspend fun setWaitMode(shouldWait: Boolean) : JniResult<Boolean>

    fun setFileStartedByPlayerListener(listener: (String) -> Unit)
    fun setPlaybackProgressListener(listener: (String, Int) -> Unit)

    fun getCurrentPosition(): Float
    fun changePlaybackPosition(newPosition: Float)
    fun resetPreSelection()
    fun isReady(): Boolean
    fun isPaused(): Boolean
    fun getState(): PlayerState //TODO reduce the state calls to this one
    fun getWaitMode() : Boolean
    fun hasLoopFile(): Boolean
    fun setHasLoopFile(hasFile: Boolean)
    fun setOnLoopedListener(receiver: (Int) -> Unit)
    fun setOnLoopSwitchedListener(receiver: () -> Unit)
    fun setSwitchingLoopsBehaviour(behaviour: SwitchingLoopsBehaviour)
    fun getSwitchingLoopsBehaviour(): SwitchingLoopsBehaviour
}
