package com.michaelpohl.service

import android.media.session.MediaSession
import android.os.Binder
import com.michaelpohl.player.JniPlayer
import com.michaelpohl.player.PlayerInterface
import com.michaelpohl.shared.JniResult
import com.michaelpohl.shared.PlayerState
import timber.log.Timber

open class PlayerServiceBinder : Binder(),
    PlayerInterface {

    init {
        Timber.d("INIT BINDER")
    }

    lateinit var session: MediaSession // TODO check if we can do without lateinit
    protected var player = JniPlayer()

    override suspend fun pause(): JniResult<Nothing> {
        return player.pause()
    }

    override suspend fun resume(): JniResult<Nothing> {

        return player.resume()
    }

    override suspend fun stop(): JniResult<Nothing> {
        return player.stop()
    }

    override suspend fun setWaitMode(shouldWait: Boolean): JniResult<Boolean> {
        return player.setWaitMode(shouldWait)
    }

    override suspend fun setSampleRate(sampleRate: Int): JniResult<Int> {
        return player.setSampleRate(sampleRate)
    }

    override fun setFileStartedByPlayerListener(listener: (String) -> Unit) {
        player.setFileStartedByPlayerListener(listener)
    }

    override fun setPlaybackProgressListener(listener: (String, Int) -> Unit) {
        player.setPlaybackProgressListener(listener)
    }

    override fun changePlaybackPosition(newPosition: Float) =
        player.changePlaybackPosition(newPosition)

    override fun resetPreSelection() = player.resetPreSelection()
    override fun isReady(): Boolean {
        return player.isReady
    }

    override fun getState(): PlayerState {
        return player.state
    }

    override fun getWaitMode(): Boolean {
        return player.waitMode
    }

    override fun setHasLoopFile(hasFile: Boolean) {
        player.hasLoopFile = hasFile
    }

    override fun setOnLoopedListener(receiver: (Int) -> Unit) {
        player.onLoopedListener = receiver
    }

    override fun getCurrentPosition() = player.getCurrentPosition()

    // TODO refactor this so it's not needed outside
    override fun hasLoopFile(): Boolean {
        return player.hasLoopFile
    }

    override suspend fun select(path: String): JniResult<String> {
        return player.select(path)
    }

    override suspend fun play(): JniResult<String> {
        return player.start()
    }
}
