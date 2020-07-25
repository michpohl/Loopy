package de.michaelpohl.loopy.ui.main.player

import android.os.Handler
import android.view.View
import androidx.lifecycle.MutableLiveData
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.PlayerState
import de.michaelpohl.loopy.common.immutable
import de.michaelpohl.loopy.common.util.coroutines.uiJob
import de.michaelpohl.loopy.model.AppStateRepository
import de.michaelpohl.loopy.model.AudioFilesRepository
import de.michaelpohl.loopy.model.DataRepository
import de.michaelpohl.loopy.model.PlayerServiceInterface
import de.michaelpohl.loopy.ui.main.BaseViewModel
import kotlinx.coroutines.cancel
import timber.log.Timber

class PlayerViewModel(private val repository: AudioFilesRepository, private val appState: AppStateRepository) :
    BaseViewModel() {

    val loopsList: List<AudioModel> = repository.getSingleSet() //TODO LiveData?

    lateinit var adapter: LoopsAdapter
    private var updateHandler = Handler()

    private val _isPlaying = MutableLiveData(false)
    val isPlaying = _isPlaying.immutable()

    private val _fileCurrentlyPlayed = MutableLiveData<String>()
    val fileCurrentlyPlayed = _fileCurrentlyPlayed.immutable()

    private val _filePreselected = MutableLiveData<String>()
    val filePreselected = _filePreselected.immutable()

    private val _emptyMessageVisibility = MutableLiveData(View.VISIBLE)
    val emptyMessageVisibility = _emptyMessageVisibility.immutable()

    private val _clearListButtonVisibility = MutableLiveData(View.GONE)
    val clearListButtonVisibility = _clearListButtonVisibility.immutable()

    private val _acceptedFileTypesAsString = MutableLiveData(DataRepository.getAllowedFileTypeListAsString())
    val acceptedFileTypesAsString = _acceptedFileTypesAsString.immutable()

    var looper: PlayerServiceInterface? = null
        set(value) {
            field = value
            setPlayerWaitModeTo(appState.isWaitMode)
        }

    private fun setPlayerWaitModeTo(shouldWait: Boolean) {
        uiJob {
            if (looper?.setWaitMode(appState.isWaitMode)?.isSuccess() == true) {
                Timber.d("Wait mode set from app state")
            } else {
                error("Failed to set wait mode. This is a program error.")
            }
        }
    }

    lateinit var playerActionsListener: PlayerActionsListener

    var currentlySelected: String? = null

    fun onStartPlaybackClicked(view: View) {
        looper?.let {
            if (it.hasLoopFile()) startLooper()
        }
    }

    fun onStopPlaybackClicked(view: View) {
        stopLooper()
    }

    fun onPausePlaybackClicked(view: View) {
        uiJob {
            looper?.let {

                if (!it.isReady()) cancel()
                if (it.isPlaying()) {
                    it.pause()
                    onPlaybackStopped()
                } else if (it.getState() == PlayerState.PAUSED) startLooper()
            }
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

    // TODO make nicer
    fun onLoopClicked(audioModel: AudioModel) {
        Timber.d("Clicked on: ${audioModel.name}")
        uiJob {
            with (looper?.select(audioModel.path)) {
                if (this?.isSuccess() == true) _filePreselected.postValue(this.data)
            }
        }
    }

    private fun startLooper() {

        // TODO here start the looper again with currentlySelected
        //        JniBridge.progressListener = { adapter.updateProgress((it.toFloat()))}
        //        JniBridge.playedFileChangedListener = {adapter.highlightPlayingFile(it)}
        //        isPlaying.set(true)
        //        looper?.start()
    }

    fun stopLooper() {
        uiJob {
            looper?.let {
                if (!it.isReady()) this.cancel()
                if (it.getState() == PlayerState.PLAYING || it.getState() == PlayerState.PAUSED) {
                    it.stop()
                }
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
        _isPlaying.value = false
    }

    interface PlayerActionsListener {

        fun onOpenFileBrowserClicked()
        fun onBrowseMediaStoreClicked()
    }
}
