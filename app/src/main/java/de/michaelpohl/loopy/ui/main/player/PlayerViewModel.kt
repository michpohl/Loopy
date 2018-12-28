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

    var isPlaying = ObservableBoolean(false)

    lateinit var settingsDropDown: WeakReference<View>
    lateinit var fileOptionsDropDown: WeakReference<View>
    lateinit var playerActionsListener: PlayerActionsListener
    lateinit var loopsList: List<FileModel>
    lateinit var pickFileTypesListener: () -> Unit

    fun getAdapter(): LoopsAdapter {
        return adapter
    }

    fun toggleFilesDropDown() {
        // close the other if still open
        if (settingsDropDownDropped) {
            slideUp(settingsDropDown.get() ?: return)
            settingsDropDownDropped = !settingsDropDownDropped
        }

        if (!filesDropDownDropped) {
            slideDown(fileOptionsDropDown.get() ?: return)
        } else {
            slideUp(fileOptionsDropDown.get() ?: return)
        }
        filesDropDownDropped = !filesDropDownDropped
    }

    fun toggleSettingsDropDown() {
        // close the other if still open
        if (filesDropDownDropped) {
            slideUp(fileOptionsDropDown.get() ?: return)
            filesDropDownDropped = !filesDropDownDropped
        }

        if (!settingsDropDownDropped) {
            slideDown(settingsDropDown.get() ?: return)
        } else {
            slideUp(settingsDropDown.get() ?: return)
        }
        settingsDropDownDropped = !settingsDropDownDropped
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
        toggleSettingsDropDown()
    }

    fun onChangeAllowedFileTypesClicked(view: View) {
        pickFileTypesListener.invoke()
        closeDropDowns()
    }

    fun onSwitchingBehaviourToggled(view: View) {
        var behaviour = LoopsRepository.settings.switchingLoopsBehaviour
        Timber.d("Clicked on switching behaviour. Current behaviour: %s", behaviour)
        if (behaviour == SwitchingLoopsBehaviour.SWITCH) {
            behaviour = SwitchingLoopsBehaviour.WAIT
            switchBehaviourButtonText.set(getString(R.string.btn_switching_behaviour_wait_to_finish))
        } else {
            behaviour = SwitchingLoopsBehaviour.SWITCH
            switchBehaviourButtonText.set(getString(R.string.btn_switching_behaviour_switch_immediately))
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

    fun onItemPreSelected(fm: FileModel, position: Int) {
        Timber.d("Preselection!!")
        adapter.notifyDataSetChanged()
    }

    fun onItemSelected(fm: FileModel, position: Int) {
        looper.setLoop(getApplication(), FileHelper.getSingleFile(fm.path))
        val oldPosition = adapter.selectedPosition
        adapter.selectedPosition = position

        // behaviour when looper should wait for the loop to finish first
        //this behaviour only makes sense when playback is running
        if (looper.switchingLoopsBehaviour == SwitchingLoopsBehaviour.WAIT && looper.isPlaying()) {
            looper.onLoopSwitchedListener = {

                // need to update all items because the asynchronous switching can mess things up
                adapter.notifyDataSetChanged()
            }
            return
        }

        //standard behaviour. Also nicer view updating style
        adapter.notifyItemChanged(oldPosition)
        adapter.notifyItemChanged(position)
        startLooper()
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
        adapter.resetProgress()
        onPlaybackStopped()
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
