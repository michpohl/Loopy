package de.michaelpohl.loopy.ui.main

import android.app.Application
import android.databinding.ObservableField
import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileHandler
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.model.LoopedPlayer
import timber.log.Timber

class PlayerViewModel(application: Application) : BaseViewModel(application) {

    var looper: LoopedPlayer = LoopedPlayer.create(application, R.raw.loop)
    private var adapter =  LoopsAdapter()
    lateinit var selectFolderListener: OnSelectFolderClickedListener
    lateinit var loopsList: List<FileModel>
    private val fileHandler= FileHandler()

    var testLabel = ObservableField<String>("hohoho")

    fun getAdapter(): LoopsAdapter {
        return adapter
    }

    fun onStartClicked(view: View) {
        testLabel.set("hahaha")
        looper.start()
    }

    fun onStopClicked(view: View) {
        testLabel.set("Hohoho")
        looper.stop()
    }

    fun onPauseClicked(view: View) {
        testLabel.set("Hihihi")
        if (looper.isPlaying()) looper.pause()
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

    fun onItemSelected(fm : FileModel) {
        looper.setLoop(fileHandler.getSingleFile(fm.path))
    }

}
