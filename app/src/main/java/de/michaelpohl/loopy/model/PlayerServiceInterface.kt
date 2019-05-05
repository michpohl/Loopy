package de.michaelpohl.loopy.model

import android.net.Uri
import de.michaelpohl.loopy.common.PlayerState
import de.michaelpohl.loopy.common.SwitchingLoopsBehaviour

interface ILooper {
    fun preselect()
    fun select()
    fun start()
    fun pause()
    fun stop()
    fun getCurrentPosition(): Float
    fun changePlaybackPosition(newPosition: Float)
    fun resetPreSelection()
    fun isReady(): Boolean
    fun isPlaying(): Boolean
    fun getState(): PlayerState
    fun setHasLoopFile(hasFile: Boolean)
    fun setOnLoopedListener(receiver: (Int) -> Unit)
    fun setOnLoopSwitchedListener(receiver: () -> Unit)
    fun setLoopUri(uri: Uri)
    fun setSwitchingLoopsBehaviour(behaviour: SwitchingLoopsBehaviour)
    fun getSwitchingLoopsBehaviour(): SwitchingLoopsBehaviour
}
