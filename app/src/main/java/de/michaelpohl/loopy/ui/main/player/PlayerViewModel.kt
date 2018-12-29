package de.michaelpohl.loopy.ui.main.player

import android.animation.ObjectAnimator
import android.app.Application
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.os.Handler
import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileHelper
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.PlayerState
import de.michaelpohl.loopy.common.SwitchingLoopsBehaviour
import de.michaelpohl.loopy.model.LoopedPlayer
import de.michaelpohl.loopy.model.LoopsRepository
import de.michaelpohl.loopy.ui.main.BaseViewModel
import de.michaelpohl.loopy.ui.main.player.PlayerItemViewModel.SelectionState
import timber.log.Timber
import java.lang.ref.WeakReference

class PlayerViewModel(application: Application) : BaseViewModel(application) {

    val overlayVisibility = ObservableField(View.GONE)

    private var adapter = LoopsAdapter(application)
    private var updateHandler = Handler()
    private var filesDropDownDropped = false
    private var settingsDropDownDropped = false

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
    var acceptedFileTypesAsString = ObservableField(LoopsRepository.getAllowedFileTypeListAsString())

    var switchBehaviourButtonText = ObservableField(
        if (LoopsRepository.settings.switchingLoopsBehaviour == SwitchingLoopsBehaviour.WAIT) {
            getString(R.string.btn_switching_behaviour_wait_to_finish)
        } else {
            getString(R.string.btn_switching_behaviour_switch_immediately)
        }
    )

    lateinit var settingsDropDown: WeakReference<View>
    lateinit var fileOptionsDropDown: WeakReference<View>
    lateinit var playerActionsListener: PlayerActionsListener
    lateinit var loopsList: List<FileModel>
    lateinit var pickFileTypesListener: () -> Unit

    fun getAdapter(): LoopsAdapter {
        return adapter
    }

    fun toggleFilesDropDown(onlySlideUp: Boolean = false) {
        // close the other if still open
        if (settingsDropDownDropped) {
            slideUp(settingsDropDown.get() ?: return)
            settingsDropDownDropped = !settingsDropDownDropped
        }

        if (!filesDropDownDropped && !onlySlideUp) {
            slideDown(fileOptionsDropDown.get() ?: return)
        } else {
            slideUp(fileOptionsDropDown.get() ?: return)
        }
        filesDropDownDropped = !filesDropDownDropped
    }

    fun toggleSettingsDropDown(onlySlideUp: Boolean = false) {
        // close the other if still open
        if (filesDropDownDropped) {
            slideUp(fileOptionsDropDown.get() ?: return)
            filesDropDownDropped = !filesDropDownDropped
        }

        if (!settingsDropDownDropped && !onlySlideUp) {
            slideDown(settingsDropDown.get() ?: return)
        } else {
            slideUp(settingsDropDown.get() ?: return)
        }
        settingsDropDownDropped = !settingsDropDownDropped
    }

    fun onStartPlaybackClicked(view: View) {
        Timber.d("Start")
        if (looper.hasLoopFile) startLooper()
    }

    fun onStopPlaybackClicked(view: View) {
        Timber.d("Stop")
        stopLooper()
    }

    fun onPausePlaybackClicked(view: View) {
        Timber.d("Pause")
        Timber.d("Is looper ready? %s", looper.isReady)
        if (!looper.isReady) return
        if (looper.isPlaying()) {
            looper.pause()
            onPlaybackStopped()
        } else if (looper.state == PlayerState.PAUSED) startLooper()
    }

    fun onClearListClicked(view: View) {
        toggleFilesDropDown()
        loopsList = emptyList()
        stopLooper()
        looper.hasLoopFile = false
        updateData()
        LoopsRepository.onLoopsListCleared()
    }

    fun onBrowseStorageClicked(view: View) {
        toggleFilesDropDown()
        playerActionsListener.onOpenFileBrowserClicked()
    }

    fun onOverlayClicked(view: View) {
        closeDropDowns()
    }

    fun onChangeAllowedFileTypesClicked(view: View) {
        pickFileTypesListener.invoke()
        closeDropDowns()
    }

    fun onSwitchingBehaviourToggled(view: View) {
        var behaviour = LoopsRepository.settings.switchingLoopsBehaviour
        if (behaviour == SwitchingLoopsBehaviour.SWITCH) {
            behaviour = SwitchingLoopsBehaviour.WAIT
            switchBehaviourButtonText.set(getString(R.string.btn_switching_behaviour_wait_to_finish))
        } else {
            behaviour = SwitchingLoopsBehaviour.SWITCH
            switchBehaviourButtonText.set(getString(R.string.btn_switching_behaviour_switch_immediately))

            resetPreSelection()
        }
        looper.switchingLoopsBehaviour = behaviour
        LoopsRepository.settings.switchingLoopsBehaviour = behaviour
        LoopsRepository.saveCurrentState()
    }

    fun updateData() {
        adapter.updateData(loopsList)
        if (adapter.itemCount != 0) {
            emptyMessageVisibility.set(View.INVISIBLE)
            clearListButtonVisibility.set(View.VISIBLE)
        } else {
            emptyMessageVisibility.set(View.VISIBLE)
            clearListButtonVisibility.set(View.GONE)
        }
        acceptedFileTypesAsString.set(LoopsRepository.getAllowedFileTypeListAsString())
    }

    //TODO beautify, strip notification into extra method in adapter
    fun onItemSelected(fm: FileModel, position: Int, selectionState: SelectionState) {
        looper.setLoop(getApplication(), FileHelper.getSingleFile(fm.path))

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

//            }
        } else {

            val oldPosition = adapter.selectedPosition
            adapter.selectedPosition = position
            adapter.notifyMultipleItems(arrayOf(oldPosition, position))
            startLooper()
        }
    }

    fun closeDropDowns(): Boolean {
        var foundOpenDropDowns = false
        if (settingsDropDownDropped) {
            slideUp(settingsDropDown.get() ?: return false)
            settingsDropDownDropped = !settingsDropDownDropped
            foundOpenDropDowns = true
        }
        if (filesDropDownDropped) {
            slideUp(fileOptionsDropDown.get() ?: return false)
            filesDropDownDropped = !filesDropDownDropped
            foundOpenDropDowns = true
        }
        return foundOpenDropDowns
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

    private fun resetPreSelection() {
        adapter.resetPreSelection()
        looper.resetPreSelection()
    }

    private fun onPlaybackStopped() {
        isPlaying.set(false)
        updateHandler.removeCallbacks(updateRunnable)
    }

    private fun slideDown(view: View) {
        overlayVisibility.set(View.VISIBLE)
        val mover = ObjectAnimator.ofFloat(view, "translationY", (view.height - 1).toFloat())
        mover.start()
    }

    private fun slideUp(view: View) {
        overlayVisibility.set(View.GONE)
        val mover = ObjectAnimator.ofFloat(view, "translationY", -(view.height - 1).toFloat())
        mover.start()
    }

    interface PlayerActionsListener {
        fun onOpenFileBrowserClicked()
    }
}
