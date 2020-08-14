package de.michaelpohl.loopy.ui.main.filebrowser

import android.view.View
import androidx.lifecycle.MutableLiveData
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.*
import de.michaelpohl.loopy.model.DataRepository
import timber.log.Timber

class FileBrowserViewModel(private val repo: StorageRepository) : BrowserViewModel() {

    private var adapter =
        FileBrowserAdapter(this::onSelectedItemsChanged, this::onItemClicked)
    lateinit var path: String

    private val _currentFiles = MutableLiveData<List<FileModel>>()
    val currentFiles = _currentFiles.immutable()

    fun getFolderContent() {
        val files = repo.getPathContent(path).toFileModels()
        if (files.isEmpty()) {
            Timber.d("Files is empty")
            _emptyFolderLayoutVisibility.postValue(View.VISIBLE)
        } else {
            Timber.d("Files is not empty")
            _emptyFolderLayoutVisibility.postValue(View.INVISIBLE)
        }
        if (repo.containsAudioFiles(path)) {
            _bottomBarVisibility.postValue(View.VISIBLE)
        } else _bottomBarVisibility.postValue(View.INVISIBLE)
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
            _selectButtonText.postValue(getString(R.string.btn_deselect_all))
        } else {
            _selectButtonText.postValue(getString(R.string.btn_select_all))
        }
        DataRepository.onFileModelSelectionUpdated(selectedItems)
    }

    private fun onItemClicked(fileModel: FileModel) {
        if (fileModel.fileType == FileType.FOLDER) listener.onFolderClicked(fileModel)
    }
}
