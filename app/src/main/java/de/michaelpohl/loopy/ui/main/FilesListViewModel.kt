package de.michaelpohl.loopy.ui.main

import android.app.Application
import android.databinding.ObservableField
import android.os.Environment
import android.view.View
import de.michaelpohl.loopy.common.FileHandler
import timber.log.Timber

class FilesListViewModel(application: Application) : BaseViewModel(application) {
    private var adapter = FilesRecyclerAdapter()
    private var filesPath = Environment.getExternalStorageDirectory().toString()
    private var fileHandler = FileHandler()

    var emptyFolderLayoutVisibility = ObservableField<Int>(View.INVISIBLE)

    fun getAdapter(): FilesRecyclerAdapter {
        return adapter
    }

    fun updateDate() {

        val files = fileHandler.getFileModelsFromFiles(fileHandler.getFilesFromPath(filesPath))
        if (files.isEmpty()) {
            emptyFolderLayoutVisibility.set(View.VISIBLE)
        } else {
            emptyFolderLayoutVisibility.set(View.INVISIBLE)
        }

        adapter.updateData(files)
    }
}