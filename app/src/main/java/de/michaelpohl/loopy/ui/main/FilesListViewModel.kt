package de.michaelpohl.loopy.ui.main

import android.app.Application
import android.databinding.ObservableField
import android.view.View
import de.michaelpohl.loopy.common.FileHandler
import timber.log.Timber

class FilesListViewModel(application: Application) : BaseViewModel(application) {
    private var adapter = FilesAdapter()
    private var fileHandler = FileHandler()
    lateinit var path: String

    var emptyFolderLayoutVisibility = ObservableField<Int>(View.INVISIBLE)

    fun getAdapter(): FilesAdapter {
        return adapter
    }

    fun updateData() {

        val files = fileHandler.getFileModelsFromFiles(fileHandler.getFilesFromPath(path))
        if (files.isEmpty()) {
            emptyFolderLayoutVisibility.set(View.VISIBLE)
        } else {
            emptyFolderLayoutVisibility.set(View.INVISIBLE)
        }
        adapter.updateData(files)
    }

    fun onSelectClicked(view: View) {
    Timber.d("Selecting item")}
}