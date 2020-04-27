package de.michaelpohl.loopy.ui.main.player

import android.net.Uri
import android.os.Handler
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.PlayerState
import de.michaelpohl.loopy.common.SwitchingLoopsBehaviour
import de.michaelpohl.loopy.common.jni.JniBridge
import de.michaelpohl.loopy.model.AudioFilesRepository
import de.michaelpohl.loopy.model.DataRepository
import de.michaelpohl.loopy.model.PlayerServiceInterface
import de.michaelpohl.loopy.ui.main.BaseViewModel
import de.michaelpohl.loopy.ui.main.player.PlayerItemViewModel.SelectionState
import timber.log.Timber

class PlayerViewModel(val repository: AudioFilesRepository) : BaseViewModel() {

    val loopsList: List<AudioModel> = repository.getSingleSet()

    lateinit var adapter: LoopsAdapter
    private var updateHandler = Handler()

    var isPlaying = ObservableBoolean(false)
    var emptyMessageVisibility = ObservableField(View.VISIBLE)
    var clearListButtonVisibility = ObservableField(View.GONE)
    var acceptedFileTypesAsString = ObservableField(DataRepository.getAllowedFileTypeListAsString())

    var looper: PlayerServiceInterface? = null
    lateinit var playerActionsListener: PlayerActionsListener
    //    lateinit var loopsList: List<AudioModel>

    fun onStartPlaybackClicked(view: View) {
                looper?.let {
                    if (it.getHasLoopFile()) startLooper()
                }
    }

    fun onStopPlaybackClicked(view: View) {
//        stopLooper()
        stopJNILooper()
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
//            stopLooper()
            stopJNILooper()
            looper?.setHasLoopFile(false)
        }
        acceptedFileTypesAsString.set(DataRepository.getAllowedFileTypeListAsString())
        looper?.setOnLoopedListener { elapsed -> adapter.onLoopsElapsedChanged(elapsed) }
    }

    fun onItemSelected(audioModel: AudioModel, position: Int, selectionState: SelectionState) {
        Timber.d("onItemSelected. Selection state: $selectionState, position: $position")
        looper?.let { serviceInterface ->
            Timber.d("I seem to have a looper")
            serviceInterface.setLoopUri(Uri.parse(audioModel.path))
            Timber.d("Setting uri to: ${audioModel.path}")
            // when just looping the looper sets a new listener to repeat the loop automatically
            // in WAIT mode (and only while playing) we replace the onLoopSwitchedListener with a different one to switch to the preselected loop
            if (selectionState == SelectionState.PRESELECTED && serviceInterface.getSwitchingLoopsBehaviour() == SwitchingLoopsBehaviour.WAIT && serviceInterface.isPlaying()) {
                val oldPosition = adapter.preSelectedPosition
                adapter.preSelectedPosition = position
                adapter.notifyMultipleItems(arrayOf(oldPosition, position))

                serviceInterface.setOnLoopSwitchedListener {
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
                Timber.d("old: $oldPosition, new: $position")
                adapter.notifyMultipleItems(arrayOf(oldPosition, position))
                if (!serviceInterface.isPaused()) {
                    Timber.d("Attempting to play natively: ${audioModel.path}")
                    startLooper()
                }
            }
        } ?: Timber.d("onItemSelected with no looper")
    }

    private fun startLooper() {
        JniBridge.progressListener = { adapter.updateProgress((it.toFloat()))}
        isPlaying.set(true)
        looper?.start()
    }

    fun stopJNILooper() {
        JniBridge.stop()
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

    fun onProgressChangedByUser(newProgress: Float) {
        looper?.changePlaybackPosition(newProgress)
    }

    private fun resetPreSelection() {
        adapter.resetPreSelection()
        looper?.resetPreSelection()
    }

    private fun onPlaybackStopped() {
        isPlaying.set(false)
    }

    interface PlayerActionsListener {

        fun onOpenFileBrowserClicked()
        fun onBrowseMediaStoreClicked()
    }
}
