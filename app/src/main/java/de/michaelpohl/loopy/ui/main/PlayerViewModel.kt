package de.michaelpohl.loopy.ui.main

import android.animation.ObjectAnimator
import android.app.Application
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.os.Handler
import android.view.View
import de.michaelpohl.loopy.common.FileHelper
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.model.LoopedPlayer
import java.lang.ref.WeakReference

class PlayerViewModel(application: Application) : BaseViewModel(application) {

    val overlayVisibility = ObservableField(View.GONE)

    private var adapter = LoopsAdapter(application)
    private var updateHandler = Handler()
    private var filesDropDownDropped = false
    private var settingsDropDownDropped = false
    lateinit var fileOptionsDropDown: WeakReference<View>
    lateinit var settingsDropDown: WeakReference<View>

    private var updateRunnable = object : Runnable {
        override fun run() {
            adapter.updateProgress(looper.getCurrentPosition())
            updateHandler.postDelayed(this, 40)
        }
    }

    var looper: LoopedPlayer = LoopedPlayer.create(application)
    var isPlaying = ObservableBoolean(false)
    var emptyMessageVisibility = ObservableField(View.VISIBLE)
    lateinit var playerActionsListener: PlayerActionsListener
    lateinit var loopsList: List<FileModel>

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

    fun onStartPlaybackClicked(view: View) {
        if (looper.hasLoopFile) startLooper()
    }

    fun onStopPlaybackClicked(view: View) {
        if (!looper.isReady) return
        looper.stop()
        onPlaybackStopped()
    }

    fun onPausePlaybackClicked(view: View) {
        if (!looper.isReady) return
        if (looper.isPlaying()) {
            looper.pause()
            onPlaybackStopped()
        } else if (looper.isPaused) startLooper()
    }

    fun onClearListClicked(view: View) {
        toggleFilesDropDown()
        loopsList = emptyList()
        updateData()
        if (looper.isPlaying()) {
            looper.stop()
            looper.hasLoopFile = false
        }
        playerActionsListener.onLoopsListCleared()
    }

    fun onBrowseStorageClicked(view: View) {
        toggleFilesDropDown()
        playerActionsListener.onOpenFileBrowserClicked()
    }

    fun onSettingsButtonClicked(view: View) {
        toggleSettingsDropDown()
    }

    fun onOverlayClicked(view: View) {
        closeDropDowns()
    }

    fun updateData() {
        adapter.updateData(loopsList)
        if (adapter.itemCount != 0) {
            emptyMessageVisibility.set(View.INVISIBLE)
        } else {
            emptyMessageVisibility.set(View.VISIBLE)
        }
    }

    fun onItemSelected(fm: FileModel, position: Int) {
        looper.setLoop(getApplication(), FileHelper.getSingleFile(fm.path))
        val oldPosition = adapter.selectedPosition
        adapter.selectedPosition = position
        adapter.notifyItemChanged(oldPosition)
        adapter.notifyItemChanged(position)
        startLooper()
    }

    fun closeDropDowns() : Boolean {
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

    private fun onPlaybackStopped() {
        isPlaying.set(false)
        updateHandler.removeCallbacks(updateRunnable)
        adapter.resetProgress()
    }

    interface PlayerActionsListener {
        fun onOpenFileBrowserClicked()
        fun onLoopsListCleared()
    }
}
