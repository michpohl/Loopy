package de.michaelpohl.loopy.ui.main.player

import android.net.Uri
import android.os.Handler
import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.PlayerState
import de.michaelpohl.loopy.common.SwitchingLoopsBehaviour
import de.michaelpohl.loopy.common.immutable
import de.michaelpohl.loopy.model.AppStateRepository
import de.michaelpohl.loopy.model.AudioFilesRepository
import de.michaelpohl.loopy.model.DataRepository
import de.michaelpohl.loopy.model.PlayerServiceInterface
import de.michaelpohl.loopy.ui.main.BaseViewModel
import de.michaelpohl.loopy.ui.main.player.PlayerItemViewModel.SelectionState
import timber.log.Timber

class PlayerViewModel(private val repository: AudioFilesRepository, private val appState: AppStateRepository) : BaseViewModel() {

    val loopsList: List<AudioModel> = repository.getSingleSet() //TODO LiveData?

    lateinit var adapter: LoopsAdapter
    private var updateHandler = Handler()

    var isPlaying = ObservableBoolean(false)

    private val _emptyMessageVisibility = MutableLiveData(View.VISIBLE)
    val emptyMessageVisibility = _emptyMessageVisibility.immutable()

    private val _clearListButtonVisibility = MutableLiveData(View.GONE)
    val clearListButtonVisibility = _clearListButtonVisibility.immutable()

    private val _acceptedFileTypesAsString = MutableLiveData(DataRepository.getAllowedFileTypeListAsString())
    val acceptedFileTypesAsString = _acceptedFileTypesAsString.immutable()

    var looper: PlayerServiceInterface? = null
    lateinit var playerActionsListener: PlayerActionsListener

    fun onStartPlaybackClicked(view: View) {
                looper?.let {
                    if (it.hasLoopFile()) startLooper()
                }
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

    fun showEmptyState(shouldShow: Boolean) {
        if (shouldShow) {
            _emptyMessageVisibility.postValue(View.VISIBLE)
            _clearListButtonVisibility.postValue(View.GONE)
        } else {
            _emptyMessageVisibility.postValue(View.INVISIBLE)
            _clearListButtonVisibility.postValue(View.VISIBLE)
//            stopLooper()
//            looper?.setHasLoopFile(false)
        }
        _acceptedFileTypesAsString.postValue(DataRepository.getAllowedFileTypeListAsString())
//        looper?.setOnLoopedListener { elapsed -> adapter.onLoopsElapsedChanged(elapsed) }
    }

    fun onLoopClicked(audioModel : AudioModel) {
        Timber.d("Clicked on: ${audioModel.name}")
                looper?.let { serviceInterface ->

                    if (appState.isWaitMode) {
                        serviceInterface.preselect(audioModel.name)
                    }
                    serviceInterface.startImmediately(audioModel.name)
                    Timber.d("Setting uri to: ${audioModel.path}")
                    // when just looping the looper sets a new listener to repeat the loop automatically
                    // in WAIT mode (and only while playing) we replace the onLoopSwitchedListener with a different one to switch to the preselected loop
//                    if (selectionState == SelectionState.PRESELECTED && serviceInterface.getSwitchingLoopsBehaviour() == SwitchingLoopsBehaviour.WAIT && serviceInterface.isPlaying()) {
//                        val oldPosition = adapter.preSelectedPosition
//                        adapter.preSelectedPosition = position
//                        adapter.notifyMultipleItems(arrayOf(oldPosition, position))
//
//                        serviceInterface.setOnLoopSwitchedListener {
//                            val oldSelected = adapter.selectedPosition
//                            adapter.selectedPosition = adapter.preSelectedPosition
//
//                            adapter.notifyMultipleItems(
//                                arrayOf(
//                                    oldSelected,
//                                    adapter.preSelectedPosition,
//                                    adapter.selectedPosition
//                                )
//                            )
//                            adapter.preSelectedPosition = -1
//                        }
//
//                        // in all other situations this standard behaviour is sufficient
//                    } else {
//
//                        val oldPosition = adapter.selectedPosition
//                        adapter.selectedPosition = position
//                        Timber.d("old: $oldPosition, new: $position")
//                        adapter.notifyMultipleItems(arrayOf(oldPosition, position))
//                        if (!serviceInterface.isPaused()) {
//                            Timber.d("Attempting to play natively: ${audioModel.path}")
//                            startLooper()
//                        }
//                    }
                } ?: Timber.d("onItemSelected with no looper")
    }

//    fun onItemSelected(audioModel: AudioModel, position: Int, selectionState: SelectionState) {
//        Timber.d("onItemSelected. Selection state: $selectionState, position: $position")
//        looper?.let { serviceInterface ->
//            Timber.d("I seem to have a looper")
//            serviceInterface.setLoopUri(Uri.parse(audioModel.path))
//            Timber.d("Setting uri to: ${audioModel.path}")
//            // when just looping the looper sets a new listener to repeat the loop automatically
//            // in WAIT mode (and only while playing) we replace the onLoopSwitchedListener with a different one to switch to the preselected loop
//            if (selectionState == SelectionState.PRESELECTED && serviceInterface.getSwitchingLoopsBehaviour() == SwitchingLoopsBehaviour.WAIT && serviceInterface.isPlaying()) {
//                val oldPosition = adapter.preSelectedPosition
//                adapter.preSelectedPosition = position
//                adapter.notifyMultipleItems(arrayOf(oldPosition, position))
//
//                serviceInterface.setOnLoopSwitchedListener {
//                    val oldSelected = adapter.selectedPosition
//                    adapter.selectedPosition = adapter.preSelectedPosition
//
//                    adapter.notifyMultipleItems(
//                        arrayOf(
//                            oldSelected,
//                            adapter.preSelectedPosition,
//                            adapter.selectedPosition
//                        )
//                    )
//                    adapter.preSelectedPosition = -1
//                }
//
//                // in all other situations this standard behaviour is sufficient
//            } else {
//
//                val oldPosition = adapter.selectedPosition
//                adapter.selectedPosition = position
//                Timber.d("old: $oldPosition, new: $position")
//                adapter.notifyMultipleItems(arrayOf(oldPosition, position))
//                if (!serviceInterface.isPaused()) {
//                    Timber.d("Attempting to play natively: ${audioModel.path}")
//                    startLooper()
//                }
//            }
//        } ?: Timber.d("onItemSelected with no looper")
//    }

    private fun startLooper() {
//        JniBridge.progressListener = { adapter.updateProgress((it.toFloat()))}
//        JniBridge.playedFileChangedListener = {adapter.highlightPlayingFile(it)}
//        isPlaying.set(true)
//        looper?.start()
    }

    fun stopLooper() {
        looper?.let {

            if (!it.isReady()) return
            if (it.getState() == PlayerState.PLAYING || it.getState() == PlayerState.PAUSED) {
                it.stop()
            }
        }
        resetPreSelection()

//        adapter.resetProgress()
        onPlaybackStopped()
    }

    fun onProgressChangedByUser(newProgress: Float) {
        looper?.changePlaybackPosition(newProgress)
    }

    private fun resetPreSelection() {
//        adapter.resetPreSelection()
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
