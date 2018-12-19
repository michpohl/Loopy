package de.michaelpohl.loopy.ui.main

import android.app.Application
import android.databinding.ObservableField
import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileHelper

class FileBrowserViewModel(application: Application) : BaseViewModel(application) {
    private var adapter = FileBrowserAdapter()
    lateinit var path: String

    var selectButtonText = ObservableField(getString(R.string.btn_select_all))
    var emptyFolderLayoutVisibility = ObservableField<Int>(View.INVISIBLE)

    fun getAdapter(): FileBrowserAdapter {
        return adapter
    }

    fun updateData() {

        val files = FileHelper.getFileModelsFromFiles(FileHelper.getFilesFromPath(path))
        if (files.isEmpty()) {
            emptyFolderLayoutVisibility.set(View.VISIBLE)
        } else {
            emptyFolderLayoutVisibility.set(View.INVISIBLE)
        }
        adapter.updateData(files)
    }

    fun onSelectAllClicked(view: View) {
        adapter.allSelected = !adapter.allSelected
        if (adapter.allSelected) {
            selectButtonText.set(getString(R.string.btn_select_all))
        } else
            selectButtonText.set(getString(R.string.btn_deselect_all))
    }
}