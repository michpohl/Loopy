package de.michaelpohl.loopy.ui.main

import android.app.Application
import android.content.SharedPreferences
import android.databinding.ObservableField
import android.os.Handler
import android.view.View
import de.michaelpohl.loopy.common.FileHelper
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.model.LoopedPlayer
import timber.log.Timber

class PlayerViewModel(application: Application) : BaseViewModel(application) {

    var looper: LoopedPlayer = LoopedPlayer.create(application)
    private var adapter = LoopsAdapter(application)
    lateinit var selectFolderListener: OnSelectFolderClickedListener
    lateinit var loopsList: List<FileModel>
    var emptyMessageVisibility = ObservableField(View.VISIBLE)

    private var updateHandler = Handler()

    private var updateRunnable = object : Runnable {
        override fun run() {
            adapter.updateProgress(looper.getCurrentPosition())
            updateHandler.postDelayed(this, 40)
        }
    }

    fun getAdapter(): LoopsAdapter {
        return adapter
    }

    fun onStartClicked(view: View) {
        if (looper.hasLoopFile) startLooper()
    }

    fun onStopClicked(view: View) {
        looper.stop()
onPlaybackStopped()    }

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
        Timber.d("Item selcted: %s, Position in List: %s", fm.name, position)
        looper.setLoop(getApplication(), FileHelper.getSingleFile(fm.path))
        adapter.selectedPosition = position
        adapter.updateData(adapter.loopsList)
        startLooper()
    }

    private fun startLooper() {
        updateRunnable.run()
        looper.start()
    }

    private fun onPlaybackStopped() {
        updateHandler.removeCallbacks(updateRunnable)

    }
}
