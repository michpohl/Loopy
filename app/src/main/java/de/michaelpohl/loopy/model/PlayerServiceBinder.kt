package de.michaelpohl.loopy.model

import android.os.Binder
import de.michaelpohl.loopy.common.PlayerState
import de.michaelpohl.loopy.common.jni.JniResult

class PlayerServiceBinder : Binder(),
    PlayerServiceInterface {

    private var looper = JniPlayer()

    override suspend fun pause(): JniResult<Nothing> {
        return looper.pause()
    }

    override suspend fun resume(): JniResult<Nothing> {
        return looper.resume()
    }

    override suspend fun stop(): JniResult<Nothing> {
        return looper.stop()
    }

    override suspend fun setWaitMode(shouldWait: Boolean): JniResult<Boolean> {
        return looper.setWaitMode(shouldWait)
    }

    override fun setFileStartedByPlayerListener(listener: (String) -> Unit) {
        looper.setFileStartedByPlayerListener(listener)
    }

    override fun setPlaybackProgressListener(listener: (String, Int) -> Unit) {
        looper.setPlaybackProgressListener(listener)
    }

    override fun changePlaybackPosition(newPosition: Float) =
        looper.changePlaybackPosition(newPosition)

    override fun resetPreSelection() = looper.resetPreSelection()

    override fun isReady(): Boolean {
        return looper.isReady
    }

    override fun getState(): PlayerState {
        return looper.state
    }

    override fun getWaitMode(): Boolean {
        return looper.waitMode
    }

    override fun setHasLoopFile(hasFile: Boolean) {
        looper.hasLoopFile = hasFile
    }

    override fun setOnLoopedListener(receiver: (Int) -> Unit) {
        looper.onLoopedListener = receiver
    }

    override fun getCurrentPosition() = looper.getCurrentPosition()

    override fun hasLoopFile(): Boolean {
        return looper.hasLoopFile
    }

    override suspend fun select(path: String): JniResult<String> {
        return looper.select(path)
    }

    override suspend fun play(): JniResult<String> {
        return looper.start()
    }

    //    // Destroy audio player.
    //    private fun destroyAudioPlayer() {
    //        if (looper.state == PlayerState.PLAYING) {
    //            looper.stop()
    //        }
    //        //TODO properly release looper
    //        //        looper.release()
    //    }
}
