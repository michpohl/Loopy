package de.michaelpohl.loopy.ui.main

import android.app.Application
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileHelper
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.FileType
import timber.log.Timber

class FileBrowserViewModel(application: Application) : BaseViewModel(application) {
    private var adapter = FileBrowserAdapter(this::onAllSelectedChanged, this::onItemClicked)
    lateinit var listener: OnItemClickListener

    lateinit var path: String

    var selectButtonText = ObservableField(getString(R.string.btn_select_all))
    var emptyFolderLayoutVisibility = ObservableField<Int>(View.INVISIBLE)
    var bottomBarVisibility = ObservableInt(View.INVISIBLE)

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

    fun onAllSelectedChanged(numberOfSelectedItems : Int) {
        Timber.d("how many are selected? %s", numberOfSelectedItems)
        if (numberOfSelectedItems > 0) {
            selectButtonText.set(getString(R.string.btn_deselect_all))
        } else
            selectButtonText.set(getString(R.string.btn_select_all))
    }

    fun onItemClicked(fileModel: FileModel) {
        if (fileModel.fileType == FileType.FOLDER) listener.onFolderClicked(fileModel)
    }

    interface OnItemClickListener {
        fun onFolderClicked(fileModel: FileModel)
        fun onFolderSelected(fileModel: FileModel)
    }
}