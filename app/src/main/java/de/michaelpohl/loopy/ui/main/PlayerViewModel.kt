package de.michaelpohl.loopy.ui.main

import android.app.Application
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.os.Handler
import android.view.View
import de.michaelpohl.loopy.common.FileHelper
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.model.LoopedPlayer
import timber.log.Timber

class PlayerViewModel(application: Application) : BaseViewModel(application) {

    private var adapter = LoopsAdapter(application)
    private var updateHandler = Handler()

    private var updateRunnable = object : Runnable {
        override fun run() {
            adapter.updateProgress(looper.getCurrentPosition())
            updateHandler.postDelayed(this, 40)
        }
    }

    var looper: LoopedPlayer = LoopedPlayer.create(application)
    var isPlaying = ObservableBoolean(false)
    var emptyMessageVisibility = ObservableField(View.VISIBLE)
    lateinit var selectFolderListener: OnSelectFolderClickedListener
    lateinit var loopsList: List<FileModel>

    fun getAdapter(): LoopsAdapter {
        return adapter
    }

    fun onStartClicked(view: View) {
        if (looper.hasLoopFile) startLooper()
    }

    fun onStopClicked(view: View) {
        looper.stop()
        onPlaybackStopped()
    }

    fun onPauseClicked(view: View) {
        if (looper.isPlaying()) {
            looper.pause()
            onPlaybackStopped()
        } else if (looper.isPaused) startLooper()
    }

    interface OnSelectFolderClickedListener {
        fun onSelectFolderClicked()
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
        Timber.d("Item selected: %s, Position in List: %s", fm.name, position)
        looper.setLoop(getApplication(), FileHelper.getSingleFile(fm.path))
        val oldPosition = adapter.selectedPosition
        adapter.selectedPosition = position
//        adapter.updateData(adapter.loopsList)
        adapter.notifyItemChanged(oldPosition)
        adapter.notifyItemChanged(position)
        startLooper()
    }

    private fun startLooper() {
        isPlaying.set(true)
        updateRunnable.run()
        looper.start()
    }

    private fun onPlaybackStopped() {
        isPlaying.set(false)
        updateHandler.removeCallbacks(updateRunnable)
    }
}
