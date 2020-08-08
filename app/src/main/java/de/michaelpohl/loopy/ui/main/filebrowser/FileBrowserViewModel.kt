package de.michaelpohl.loopy.ui.main.filebrowser

import android.view.View
import androidx.lifecycle.MutableLiveData
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.StorageRepository
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.FileType
import de.michaelpohl.loopy.common.immutable
import de.michaelpohl.loopy.model.DataRepository

class FileBrowserViewModel(private val repo: StorageRepository) : BrowserViewModel() {

    private var adapter =
        FileBrowserAdapter(this::onSelectedItemsChanged, this::onItemClicked)
    lateinit var path: String

    private val _currentFiles = MutableLiveData<List<FileModel>>()
    val currentFiles = _currentFiles.immutable()

    init {

    }

    fun getFolderContent() {
        val files = repo.getFileModelsFromFiles(repo.getPathContent(path))
        if (files.isEmpty()) {
            emptyFolderLayoutVisibility.set(View.VISIBLE)
        } else {
            emptyFolderLayoutVisibility.set(View.INVISIBLE)
        }
        if (repo.containsAudioFiles(path)) {
            bottomBarVisibility.set(View.VISIBLE)
        } else bottomBarVisibility.set(View.INVISIBLE)
        _currentFiles.postValue(files)
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
