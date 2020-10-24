package de.michaelpohl.loopy.ui.main.player

import android.view.View
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.PlayerState.*
import de.michaelpohl.loopy.common.toVisibility
import de.michaelpohl.loopy.common.util.coroutines.uiJob
import de.michaelpohl.loopy.model.AppStateRepository
import de.michaelpohl.loopy.model.AudioFilesRepository
import de.michaelpohl.loopy.model.PlayerServiceInterface
import de.michaelpohl.loopy.ui.main.base.BaseUIState
import de.michaelpohl.loopy.ui.main.base.BaseViewModel
import de.michaelpohl.loopy.ui.main.settings.SettingsViewModel
import timber.log.Timber

class PlayerViewModel(
    private val repository: AudioFilesRepository,
    private val appState: AppStateRepository
) :
    BaseViewModel<PlayerViewModel.UIState>() {

    override fun initUIState(): UIState {
        val s = UIState(
            loopsList = repository.getSingleSet().toMutableList(),
            isPlaying = false,
            clearButtonVisibility = 0
        )
        Timber.d("uistate: $s")
        return s

    }

    init {
        _state.value = initUIState()
    }

    private lateinit var looper: PlayerServiceInterface

    private fun onPlayerSwitchedToNextFile(filename: String) {
        _state.postValue(currentState.copy(fileInFocus = filename))
    }

    private fun setPlayerWaitModeTo(shouldWait: Boolean = appState.settings.isWaitMode) {
        uiJob {
            if (looper.setWaitMode(shouldWait).isSuccess()) {
                Timber.v("Looper waitmode set to $shouldWait")
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
            setPlaybackProgressListener { name, value ->
                _state.postValue(currentState.copy(playbackProgress = Pair(name, value)))
            }
        }
        setPlayerWaitModeTo(appState.settings.isWaitMode)
    }

    fun onStartPlaybackClicked(view: View) {
        if (looper.hasLoopFile()) startLooper()
    }

    fun onStopPlaybackClicked(view: View) {
        when (looper.getState()) {
            PLAYING, PAUSED -> stopLooper()
            else -> { /* do nothing */
            }
        }
    }

    fun onPausePlaybackClicked(view: View) {
        uiJob {
            when (looper.getState()) {
                PLAYING -> looper.pause()
                PAUSED -> looper.resume()
                else -> { /* do nothing */
                }
            }

        }
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
        if (looper.getWaitMode()) {
            when (looper.getState()) {
                PLAYING, PAUSED, READY -> _state.postValue(currentState.copy(filePreselected = filename))
                STOPPED, UNKNOWN -> startLooper()
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
                        _state.postValue(currentState.copy(fileInFocus = this.data))
                    }
                }
            }
        }
    }

    fun stopLooper() {
        uiJob {
            if (looper.stop().isSuccess()) {
                _state.postValue(
                    currentState.copy(
                        playbackProgress = Pair(currentState.fileInFocus ?: "", 0),
                        fileInFocus = "",
                        filePreselected = ""
                    )
                )
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
        _state.postValue(currentState.copy(isPlaying = false))
    }

    fun addNewLoops(newLoops: List<AudioModel>) {
        // TODO ask the user if adding or replacing is desired
        repository.addLoopsToSet(newLoops)
        val currentLoops = currentState.loopsList.toMutableList()
        currentLoops.addAll(newLoops)
        _state.postValue(currentState.copy(loopsList = currentLoops))
        repository.saveLoopSelection(currentLoops)
    }

    fun onDeleteLoopClicked(audioModel: AudioModel) {
        val currentLoops = currentState.loopsList.toMutableList()
        currentLoops.remove(audioModel)
        _state.postValue(currentState.copy(loopsList = currentLoops))
        repository.saveLoopSelection(currentLoops)
    }

    data class UIState(
        val loopsList: MutableList<AudioModel>,
        val isPlaying: Boolean,
        val fileInFocus: String? = null,
        val filePreselected: String? = null,
        val playbackProgress: Pair<String, Int>? = null,
        val clearButtonVisibility: Int = View.GONE
    ) : BaseUIState() {
        val emptyMessageVisibility: Int = this.loopsList.isEmpty().toVisibility()
    }

    interface PlayerActionsListener {
        fun onOpenFileBrowserClicked()
        fun onBrowseMediaStoreClicked()
    }
}
