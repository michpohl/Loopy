package de.michaelpohl.loopy.ui.main.browser

import android.app.Application
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileHelper
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.FileType
import de.michaelpohl.loopy.model.DataRepository
import de.michaelpohl.loopy.ui.main.BaseViewModel
import timber.log.Timber

class FileBrowserViewModel(application: Application) : BaseViewModel(application) {

    private var adapter =
        FileBrowserAdapter(this::onSelectedItemsChanged, this::onItemClicked)

    var selectButtonText = ObservableField(getString(R.string.btn_select_all))
    var emptyFolderLayoutVisibility = ObservableField<Int>(View.INVISIBLE)
    var bottomBarVisibility = ObservableInt(View.INVISIBLE)

    lateinit var listener: OnItemClickListener
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

    fun onSelectButtonClicked(view: View) {
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
        DataRepository.onFileSelectionUpdated(selectedItems)
    }

    private fun onItemClicked(fileModel: FileModel) {
        if (fileModel.fileType == FileType.FOLDER) listener.onFolderClicked(fileModel)
    }

    interface OnItemClickListener {
        fun onFolderClicked(fileModel: FileModel)
    }
}