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
import de.michaelpohl.loopy.model.LoopedPlayer
import de.michaelpohl.loopy.ui.main.BaseViewModel
import de.michaelpohl.loopy.ui.main.player.PlayerItemViewModel.SelectionState
import timber.log.Timber

class PlayerViewModel(application: Application) : BaseViewModel(application) {


    private var adapter = LoopsAdapter(application, this::onProgressChangedByUser)
    private var updateHandler = Handler()


    private var updateRunnable = object : Runnable {
        override fun run() {
            adapter.updateProgress(looper.getCurrentPosition())
            updateHandler.postDelayed(this, 40)
        }
    }

    var isPlaying = ObservableBoolean(false)
    var looper: LoopedPlayer = LoopedPlayer.create(application)
    var emptyMessageVisibility = ObservableField(View.VISIBLE)
    var clearListButtonVisibility = ObservableField(View.GONE)
    var acceptedFileTypesAsString = ObservableField(DataRepository.getAllowedFileTypeListAsString())


    lateinit var playerActionsListener: PlayerActionsListener
    lateinit var loopsList: List<AudioModel>

    fun getAdapter(): LoopsAdapter {
        return adapter
    }

    fun onStartPlaybackClicked(view: View) {
        if (looper.hasLoopFile) startLooper()
    }

    fun onStopPlaybackClicked(view: View) {
        stopLooper()
    }

    fun onPausePlaybackClicked(view: View) {
        if (!looper.isReady) return
        if (looper.isPlaying()) {
            looper.pause()
            onPlaybackStopped()
        } else if (looper.state == PlayerState.PAUSED) startLooper()
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
            looper.hasLoopFile = false
        }
        acceptedFileTypesAsString.set(DataRepository.getAllowedFileTypeListAsString())
        looper.onLoopedListener = {it -> adapter.onLoopsElapsedChanged(it)}
    }

    fun onItemSelected(audioModel: AudioModel, position: Int, selectionState: SelectionState) {
        Timber.d("Selected item's uri: %s", Uri.parse(audioModel.path))
        looper.setLoopUri(Uri.parse(audioModel.path))

        if (selectionState == SelectionState.PRESELECTED && looper.switchingLoopsBehaviour == SwitchingLoopsBehaviour.WAIT && looper.isPlaying()) {
            val oldPosition = adapter.preSelectedPosition
            adapter.preSelectedPosition = position
            adapter.notifyMultipleItems(arrayOf(oldPosition, position))

            looper.onLoopSwitchedListener = {
                val oldSelected = adapter.selectedPosition
                adapter.selectedPosition = adapter.preSelectedPosition

                adapter.notifyMultipleItems(arrayOf(oldSelected, adapter.preSelectedPosition, adapter.selectedPosition))
                adapter.preSelectedPosition = -1
            }
        } else {

            val oldPosition = adapter.selectedPosition
            adapter.selectedPosition = position
            adapter.notifyMultipleItems(arrayOf(oldPosition, position))
            startLooper()
        }
    }

    private fun startLooper() {
        isPlaying.set(true)
        updateRunnable.run()
        looper.start()
    }

    private fun stopLooper() {
        if (!looper.isReady) return
        if (looper.state == PlayerState.PLAYING || looper.state == PlayerState.PAUSED) {
            looper.stop()
        }

        resetPreSelection()


        adapter.resetProgress()
        onPlaybackStopped()
    }

    private fun onProgressChangedByUser(newProgress: Float) {
        looper.changePlaybackPosition(newProgress)
    }

    private fun resetPreSelection() {
        adapter.resetPreSelection()
        looper.resetPreSelection()
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
