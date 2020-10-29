package de.michaelpohl.loopy.ui.main.player

import android.view.View
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.PlayerState.*
import de.michaelpohl.loopy.common.Settings
import de.michaelpohl.loopy.common.toVisibility
import de.michaelpohl.loopy.common.util.coroutines.uiJob
import de.michaelpohl.loopy.model.AppStateRepository
import de.michaelpohl.loopy.model.AudioFilesRepository
import de.michaelpohl.loopy.model.PlayerServiceInterface
import de.michaelpohl.loopy.ui.main.base.BaseUIState
import de.michaelpohl.loopy.ui.main.base.BaseViewModel
import timber.log.Timber

class PlayerViewModel(
    private val audioFilesRepository: AudioFilesRepository,
    private val appStateRepo: AppStateRepository
) :
    BaseViewModel<PlayerViewModel.UIState>() {
    private lateinit var settings: Settings
    private lateinit var looper: PlayerServiceInterface
    lateinit var playerActionsListener: PlayerActionsListener

    override fun initUIState(): UIState {
        return UIState(
            loopsList = audioFilesRepository.getSingleSet().toMutableList(),
            isPlaying = false,
            clearButtonVisibility = 0,
            settings = settings
        )
    }

    override fun onFragmentResumed() {
        settings = appStateRepo.settings
        Timber.d("settings: $settings")
        _state.value = initUIState()
    }

    fun setPlayerWaitMode() {
        val shouldWait: Boolean = appStateRepo.settings.isWaitMode
        if (!::looper.isInitialized || looper.getWaitMode() == shouldWait) return
        uiJob {
            if (looper.setWaitMode(shouldWait).isSuccess()) {
                Timber.v("Looper waitmode set to $shouldWait")
            } else {
                error("Failed to set wait mode. This is a program error.")
            }
            if (!looper.getWaitMode()) _state.postValue(currentState.copy(filePreselected = ""))
        }
    }

    fun setPlayer(player: PlayerServiceInterface) {
        looper = player.apply {
            setFileStartedByPlayerListener { onPlayerSwitchedToNextFile(it) }
            setPlaybackProgressListener { name, value ->
                _state.postValue(currentState.copy(playbackProgress = Pair(name, value)))
            }
        }
    }

    fun onStartPlaybackClicked(view: View) {
        uiJob {
            when (looper.getState()) {
                PLAYING -> { /* do nothing */
                }
                PAUSED -> looper.resume()
                else -> if (looper.hasLoopFile()) startLooper()
            }
        }
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

    fun addNewLoops(newLoops: List<AudioModel>) {
        // TODO ask the user if adding or replacing is desired
        audioFilesRepository.addLoopsToSet(newLoops)
        val currentLoops = currentState.loopsList.toMutableList()
        currentLoops.addAll(newLoops)
        _state.postValue(currentState.copy(loopsList = currentLoops))
        audioFilesRepository.saveLoopSelection(currentLoops)
    }

    fun onDeleteLoopClicked(audioModel: AudioModel) {
        val currentLoops = currentState.loopsList.toMutableList()
        currentLoops.remove(audioModel)
        _state.postValue(currentState.copy(loopsList = currentLoops))
        audioFilesRepository.saveLoopSelection(currentLoops)
    }

    fun onProgressChangedByUser(newProgress: Float) {
        looper.changePlaybackPosition(newProgress)
    }

    private fun onPlayerSwitchedToNextFile(filename: String) {
        _state.postValue(currentState.copy(fileInFocus = filename))
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

    @Deprecated("I assume?")
    private fun onPlaybackStopped() {
        _state.postValue(currentState.copy(isPlaying = false))
    }

    data class UIState(
        val loopsList: MutableList<AudioModel>,
        val isPlaying: Boolean,
        val isWaitMode: Boolean = false,
        val fileInFocus: String? = null,
        val filePreselected: String? = null,
        val playbackProgress: Pair<String, Int>? = null,
        val clearButtonVisibility: Int = View.GONE,
        val settings: Settings
    ) : BaseUIState() {
        val emptyMessageVisibility: Int = this.loopsList.isEmpty().toVisibility()
    }

    interface PlayerActionsListener {
        fun onOpenFileBrowserClicked()
        fun onBrowseMediaStoreClicked()
    }
}
