package de.michaelpohl.loopy.ui.main.player

import android.app.Application
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.net.Uri
import android.os.Handler
import android.view.View
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.PlayerState
import de.michaelpohl.loopy.common.SwitchingLoopsBehaviour
import de.michaelpohl.loopy.model.DataRepository
import de.michaelpohl.loopy.model.ILooper
import de.michaelpohl.loopy.ui.main.BaseViewModel
import de.michaelpohl.loopy.ui.main.player.PlayerItemViewModel.SelectionState
import timber.log.Timber

class PlayerViewModel(application: Application) : BaseViewModel(application) {

    private var adapter = LoopsAdapter(application, this::onProgressChangedByUser)
    private var updateHandler = Handler()

    private var updateRunnable = object : Runnable {
        override fun run() {
            looper?.getCurrentPosition()?.let {
                adapter.updateProgress(it)
            }
            updateHandler.postDelayed(this, 40)
        }
    }

    var isPlaying = ObservableBoolean(false)
    //    var looper: LoopedPlayer = LoopedPlayer.create(application)
    var emptyMessageVisibility = ObservableField(View.VISIBLE)
    var clearListButtonVisibility = ObservableField(View.GONE)
    var acceptedFileTypesAsString = ObservableField(DataRepository.getAllowedFileTypeListAsString())

    var looper: ILooper? = null
    lateinit var playerActionsListener: PlayerActionsListener
    lateinit var loopsList: List<AudioModel>

    fun getAdapter(): LoopsAdapter {
        return adapter
    }

    fun onStartPlaybackClicked(view: View) {
//        if (looper.setHasLoopFile) startLooper()
    }

    fun onStopPlaybackClicked(view: View) {
        stopLooper()
    }

    fun onPausePlaybackClicked(view: View) {
        looper?.let {

            if (!it.isReady()) return
            if (it.isPlaying()) {
                it.pause()
                onPlaybackStopped()
            } else if (it.getState() == PlayerState.PAUSED) startLooper()
        }
    }

    fun updateData() {
        adapter.updateData(loopsList)
        if (adapter.itemCount != 0) {
            emptyMessageVisibility.set(View.INVISIBLE)
            clearListButtonVisibility.set(View.VISIBLE)
        } else {
            emptyMessageVisibility.set(View.VISIBLE)
            clearListButtonVisibility.set(View.GONE)
            stopLooper()
            looper?.setHasLoopFile(false)
        }
        acceptedFileTypesAsString.set(DataRepository.getAllowedFileTypeListAsString())
        looper?.setOnLoopedListener { elapsed -> adapter.onLoopsElapsedChanged(elapsed) }
    }

    fun onItemSelected(audioModel: AudioModel, position: Int, selectionState: SelectionState) {
        Timber.d("onItemSelected")
        looper?.let {
            Timber.d("I seem to have a looper")
            it.setLoopUri(Uri.parse(audioModel.path))

            // when just looping the looper sets a new listener to repeat the loop automatically
            // in WAIT mode (and only while playing) we replace the onLoopSwitchedListener with a different one to switch to the preselected loop
            if (selectionState == SelectionState.PRESELECTED && it.getSwitchingLoopsBehaviour() == SwitchingLoopsBehaviour.WAIT && it.isPlaying()) {
                val oldPosition = adapter.preSelectedPosition
                adapter.preSelectedPosition = position
                adapter.notifyMultipleItems(arrayOf(oldPosition, position))

                it.setOnLoopSwitchedListener {
                    val oldSelected = adapter.selectedPosition
                    adapter.selectedPosition = adapter.preSelectedPosition

                    adapter.notifyMultipleItems(
                        arrayOf(
                            oldSelected,
                            adapter.preSelectedPosition,
                            adapter.selectedPosition
                        )
                    )
                    adapter.preSelectedPosition = -1
                }

                // in all other situations this standard behaviour is sufficient
            } else {

                val oldPosition = adapter.selectedPosition
                adapter.selectedPosition = position
                adapter.notifyMultipleItems(arrayOf(oldPosition, position))
                startLooper()
            }
        }
    }

    private fun startLooper() {
        isPlaying.set(true)
        updateRunnable.run()
        looper?.start()
    }

    fun stopLooper() {
        looper?.let {

            if (!it.isReady()) return
            if (it.getState() == PlayerState.PLAYING || it.getState() == PlayerState.PAUSED) {
                it.stop()
            }
        }
        resetPreSelection()

        adapter.resetProgress()
        onPlaybackStopped()
    }

    private fun onProgressChangedByUser(newProgress: Float) {
        looper?.changePlaybackPosition(newProgress)
    }

    private fun resetPreSelection() {
        adapter.resetPreSelection()
        looper?.resetPreSelection()
    }

    private fun onPlaybackStopped() {
        isPlaying.set(false)
        updateHandler.removeCallbacks(updateRunnable)
    }

    interface PlayerActionsListener {

        fun onOpenFileBrowserClicked()
        fun onBrowseMediaStoreClicked()
    }
}
