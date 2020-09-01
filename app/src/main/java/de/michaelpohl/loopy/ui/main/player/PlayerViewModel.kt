package de.michaelpohl.loopy.ui.main.player

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
import timber.log.Timber

class PlayerViewModel(private val repository: AudioFilesRepository, private val appState: AppStateRepository) :
    BaseViewModel() {

    val loopsList: MutableList<AudioModel> = repository.getSingleSet().toMutableList() //TODO LiveData?

    private val _isPlaying = MutableLiveData(false)
    val isPlaying = _isPlaying.immutable()

    private val _fileCurrentlyPlayed = MutableLiveData<String>()
    val fileCurrentlyPlayed = _fileCurrentlyPlayed.immutable()

    private val _filePreselected = MutableLiveData<String>()
    val filePreselected = _filePreselected.immutable()

    private val _playbackProgress = MutableLiveData<Pair<String, Int>>()
    val playbackProgress = _playbackProgress.immutable()

    private val _emptyMessageVisibility = MutableLiveData(View.VISIBLE)
    val emptyMessageVisibility = _emptyMessageVisibility.immutable()

    private val _clearListButtonVisibility = MutableLiveData(View.GONE)
    val clearListButtonVisibility = _clearListButtonVisibility.immutable()

    private val _acceptedFileTypesAsString = MutableLiveData(DataRepository.getAllowedFileTypeListAsString())
    val acceptedFileTypesAsString = _acceptedFileTypesAsString.immutable()

    lateinit var looper: PlayerServiceInterface

    private fun onPlayerSwitchedToNextFile(filename: String) {
        _fileCurrentlyPlayed.postValue(filename)
    }

    private fun setPlayerWaitModeTo(shouldWait: Boolean) {
        uiJob {
            if (looper.setWaitMode(appState.settings.isWaitMode).isSuccess()) {
            } else {
                error("Failed to set wait mode. This is a program error.")
            }
        }
    }

    lateinit var playerActionsListener: PlayerActionsListener

    var currentlySelected: String? = null

    fun setPlayer(player: PlayerServiceInterface) {
        looper = player.apply {
            setFileStartedByPlayerListener { onPlayerSwitchedToNextFile(it) }
            setPlaybackProgressListener { name, value -> _playbackProgress.postValue(Pair(name, value)) }
        }
        setPlayerWaitModeTo(appState.settings.isWaitMode)
    }

    fun onStartPlaybackClicked(view: View) {
        if (looper.hasLoopFile()) startLooper()
    }

    fun onStopPlaybackClicked(view: View) {
        when (looper.getState()) {
            PlayerState.PLAYING, PlayerState.PAUSED -> stopLooper()
            else -> { /* do nothing */
            }
        }
    }

    fun onPausePlaybackClicked(view: View) {
        uiJob {
            when (looper.getState()) {
                PlayerState.PLAYING -> looper.pause()
                PlayerState.PAUSED -> looper.resume()
                else -> { /* do nothing */ }
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
        uiJob {
            with(looper.select(audioModel.path)) {
                if (this.isSuccess()) {
                    this.data?.let {
                        onFileSelected(it)
                    } ?: error("Got no filename back from JNI. This shouldn't happen")
                }
            }
        }
    }

    private fun onFileSelected(filename: String) {
        Timber.d("isWaitmode: ${looper.getWaitMode()}")
        Timber.d("Selected: $filename, looper state: ${looper.getState()}, wait: ${looper.getWaitMode()}")
        if (looper.getWaitMode()) {
            when (looper.getState()) {
                PlayerState.PLAYING -> _filePreselected.postValue(filename)
                PlayerState.PAUSED -> _filePreselected.postValue(filename)
                PlayerState.STOPPED -> startLooper()
                PlayerState.UNKNOWN -> startLooper()
                PlayerState.READY -> _filePreselected.postValue(filename)
            }
        } else {
            startLooper()
        }
    }

    private fun startLooper() {
        uiJob {
            with(looper.play()) {
                if (this.isSuccess()) {
                    this.data?.let {
                        Timber.d("Started: $it")
                        _fileCurrentlyPlayed.postValue(this.data)
                    }
                }
            }
        }
    }

    fun stopLooper() {
        uiJob {
            if (looper.stop().isSuccess()) {
                _playbackProgress.postValue(Pair(fileCurrentlyPlayed.value ?: "", 0))
                _fileCurrentlyPlayed.postValue("")
                _filePreselected.postValue("")
            }
        }
    }

    fun onProgressChangedByUser(newProgress: Float) {
        looper.changePlaybackPosition(newProgress)
    }

    private fun resetPreSelection() {
        //        adapter.resetPreSelection()
        looper.resetPreSelection()
    }

    private fun onPlaybackStopped() {
        _isPlaying.value = false
    }

    fun addNewLoops(newLoops: List<AudioModel>) {
        // TODO ask the user if adding or replacing is desired
        repository.addLoopsToSet(newLoops)
        loopsList.addAll(newLoops)
        repository.saveLoopSelection(loopsList)
    }

    fun onDeleteLoopClicked(audioModel: AudioModel) {
        TODO("Not yet implemented")
    }

    interface PlayerActionsListener {
        fun onOpenFileBrowserClicked()
        fun onBrowseMediaStoreClicked()
    }
}
