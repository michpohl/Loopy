package de.michaelpohl.loopy.ui.main

import android.app.Application
import android.view.View
import de.michaelpohl.loopy.common.FileHandler
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.model.LoopedPlayer
import timber.log.Timber

class PlayerViewModel(application: Application) : BaseViewModel(application) {

    var looper: LoopedPlayer = LoopedPlayer.create(application)
    private var adapter = LoopsAdapter(application)
    lateinit var selectFolderListener: OnSelectFolderClickedListener
    lateinit var loopsList: List<FileModel>
    private val fileHandler = FileHandler()

    fun getAdapter(): LoopsAdapter {
        return adapter
    }

    fun onStartClicked(view: View) {
        if (looper.hasLoopFile) looper.start()
    }

    fun onStopClicked(view: View) {
        looper.stop()
    }

    fun onPauseClicked(view: View) {
        if (looper.isPlaying()) looper.pause() else if (looper.isPaused) looper.start()
    }

    fun onSelectFolderClicked(view: View) {
        Timber.d("Clicked on Select Folder")
        selectFolderListener.onSelectFolderClicked()
    }

    interface OnSelectFolderClickedListener {
        fun onSelectFolderClicked()
    }

    fun updateData() {
        adapter.updateData(loopsList)
    }

    fun onItemSelected(fm: FileModel, position: Int) {
        looper.setLoop(getApplication(), fileHandler.getSingleFile(fm.path))
        adapter.selectedPosition = position
        adapter.updateData(adapter.loopsList)
        looper.start()
    }


}
