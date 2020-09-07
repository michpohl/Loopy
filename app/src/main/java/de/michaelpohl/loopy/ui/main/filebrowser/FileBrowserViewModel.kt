package de.michaelpohl.loopy.ui.main.filebrowser

import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.StorageRepository
import de.michaelpohl.loopy.common.immutable
import de.michaelpohl.loopy.common.toFileModels
import de.michaelpohl.loopy.ui.main.BaseViewModel
import timber.log.Timber

open class FileBrowserViewModel(private val repo: StorageRepository) : BaseViewModel() {

    private val _filesToDisplay = MutableLiveData<List<FileModel>>()
    val filesToDisplay = _filesToDisplay.immutable()

    private val lastDisplayedFiles = mutableListOf<List<FileModel>>()

    // TODO this doesn't seem to be properly connected yet
    var bottomBarVisibility = MediatorLiveData<Int>()

    lateinit var onSelectionSubmittedListener: (List<FileModel.AudioFile>) -> Unit

    private var _emptyFolderLayoutVisibility =
        MutableLiveData(View.INVISIBLE) //override if interested
    var emptyFolderLayoutVisibility = _emptyFolderLayoutVisibility.immutable()

    private var _selectButtonText = MutableLiveData(getString(R.string.btn_select_all))
    var selectButtonText = _selectButtonText.immutable()

    private val selectedFiles = MutableLiveData<List<FileModel.AudioFile>>()

    fun getFolderContent(path: String) {
        val files = repo.getPathContent(path).toFileModels()
        if (files.isEmpty()) {
            _emptyFolderLayoutVisibility.postValue(View.VISIBLE)
        } else {
            _emptyFolderLayoutVisibility.postValue(View.INVISIBLE)
        }
        lastDisplayedFiles.add(filesToDisplay.value.orEmpty())
        _filesToDisplay.postValue(files)

    }

    fun onFolderClicked(folder: FileModel.Folder) {
        getFolderContent(folder.path)
    }

    fun onFileSelectionChanged(fileModel: FileModel.AudioFile, isSelected: Boolean) {
        val currentList = selectedFiles.value.orEmpty().toMutableList()
        if (isSelected) {
            currentList.add(fileModel)
        } else {
            currentList.remove(fileModel)
        }
        selectedFiles.postValue(currentList)
        Timber.d("Currently selected: ${currentList.map { it.name }}")
    }

    fun onOpenSelectionClicked(v: View) {
        onSelectionSubmittedListener(selectedFiles.value.orEmpty())
    }

    fun onBackPressed(): Boolean {
        with(lastDisplayedFiles) {
            return if (this.isNotEmpty()) {
                _filesToDisplay.postValue(this.last())
                remove(this.last())
                true
            } else false
        }
    }
}
