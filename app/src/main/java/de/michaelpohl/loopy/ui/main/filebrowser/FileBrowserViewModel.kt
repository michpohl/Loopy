package de.michaelpohl.loopy.ui.main.filebrowser

import android.app.Application
import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileHelper
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.FileType
import de.michaelpohl.loopy.model.DataRepository

class FileBrowserViewModel(application: Application) : BrowserViewModel(application) {

    private var adapter =
        FileBrowserAdapter(this::onSelectedItemsChanged, this::onItemClicked)

    lateinit var path: String

    fun getAdapter(): FileBrowserAdapter {
        return adapter
    }

    fun updateAdapter() {
        val files = FileHelper.getFileModelsFromFiles(FileHelper.getFilesFromPath(path))
        if (files.isEmpty()) {
            emptyFolderLayoutVisibility.set(View.VISIBLE)
        } else {
            emptyFolderLayoutVisibility.set(View.INVISIBLE)
        }
        if (FileHelper.containsAudioFiles(path)) {
            bottomBarVisibility.set(View.VISIBLE)
        } else bottomBarVisibility.set(View.INVISIBLE)
        adapter.updateData(files)
    }

    override fun onSelectButtonClicked(view: View) {
        if (adapter.selectedItems.size > 0) {
            adapter.deselectAll()
        } else {
            adapter.selectAll()
        }
    }

    private fun onSelectedItemsChanged(selectedItems: List<FileModel>) {
        if (selectedItems.isNotEmpty()) {
            selectButtonText.set(getString(R.string.btn_deselect_all))
        } else {
            selectButtonText.set(getString(R.string.btn_select_all))
        }
        DataRepository.onFileModelSelectionUpdated(selectedItems)
    }

    private fun onItemClicked(fileModel: FileModel) {
        if (fileModel.fileType == FileType.FOLDER) listener.onFolderClicked(fileModel)
    }
}