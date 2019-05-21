package de.michaelpohl.loopy.model

import android.content.Context
import android.net.Uri
import android.os.Binder
import de.michaelpohl.loopy.common.PlayerState
import de.michaelpohl.loopy.common.SwitchingLoopsBehaviour
import timber.log.Timber

class PlayerServiceBinder(serviceContext: Context) : Binder(),
    PlayerServiceInterface {

    private var looper = LoopedPlayer.create(serviceContext)

    override fun start() {
        Timber.d("Start in Binder")
        looper.start()
    }

    override fun pause() {
        looper.pause()
    }

    override fun stop() {
        looper.stop()
        destroyAudioPlayer()
    }

    override fun changePlaybackPosition(newPosition: Float) =
        looper.changePlaybackPosition(newPosition)

    override fun resetPreSelection() = looper.resetPreSelection()

    override fun isReady(): Boolean {
        return looper.isReady
    }

    override fun isPlaying(): Boolean {
        return looper.isPlaying()
    }

    override fun isPaused(): Boolean {
        return looper.state == PlayerState.PAUSED
    }

    override fun getState(): PlayerState {
        return looper.state
    }

    override fun setHasLoopFile(hasFile: Boolean) {
        looper.hasLoopFile = hasFile
    }

    override fun setOnLoopedListener(receiver: (Int) -> Unit) {
        looper.onLoopedListener = receiver
    }

    override fun setOnLoopSwitchedListener(receiver: () -> Unit) {
        looper.onLoopSwitchedListener = receiver
    }

    override fun setLoopUri(uri: Uri) = looper.setLoopUri(uri)

    override fun setSwitchingLoopsBehaviour(behaviour: SwitchingLoopsBehaviour) {
        looper.switchingLoopsBehaviour = behaviour
    }

    override fun getSwitchingLoopsBehaviour(): SwitchingLoopsBehaviour {
        return looper.switchingLoopsBehaviour
    }

    override fun getCurrentPosition() = looper.getCurrentPosition()

    override fun getHasLoopFile(): Boolean {
        return looper.hasLoopFile
    }

    override fun preselect() {
    }

    override fun select() {
    }

    // Destroy audio player.
    private fun destroyAudioPlayer() {
        if (looper.state == PlayerState.PLAYING) {
            looper.stop()
        }
//TODO properly release looper
//        looper.release()
    }
}
