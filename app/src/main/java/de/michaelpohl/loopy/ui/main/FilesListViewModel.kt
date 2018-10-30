package de.michaelpohl.loopy.ui.main

import android.app.Application
import android.databinding.ObservableField
import android.os.Environment
import android.view.View
import de.michaelpohl.loopy.common.FileHandler

class FilesListViewModel(application: Application) : BaseViewModel(application) {
    private var adapter = FilesAdapter()
    private var fileHandler = FileHandler()
    private lateinit var path: String

    var emptyFolderLayoutVisibility = ObservableField<Int>(View.INVISIBLE)

    fun getAdapter(): FilesAdapter {
        return adapter
    }

    fun updateData() {

        val files = fileHandler.getFileModelsFromFiles(fileHandler.getFilesFromPath())
        if (files.isEmpty()) {
            emptyFolderLayoutVisibility.set(View.VISIBLE)
        } else {
            emptyFolderLayoutVisibility.set(View.INVISIBLE)
        }

        adapter.updateData(files)
    }
}