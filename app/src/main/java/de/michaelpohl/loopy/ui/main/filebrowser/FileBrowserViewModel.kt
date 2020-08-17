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

class FileBrowserViewModel(private val repo: StorageRepository) : BaseViewModel() {

    private val _currentFiles = MutableLiveData<List<FileModel>>()
    val currentFiles = _currentFiles.immutable()

    var bottomBarVisibility = MediatorLiveData<Int>()

    lateinit var onSelectionSubmittedListener: (List<FileModel.AudioFile>) -> Unit

    protected var _emptyFolderLayoutVisibility =
        MutableLiveData(View.INVISIBLE) //override if interested
    var emptyFolderLayoutVisibility = _emptyFolderLayoutVisibility.immutable()

    var _selectButtonText = MutableLiveData(getString(R.string.btn_select_all))
    var selectButtonText = _selectButtonText.immutable()

    private val selectedFiles = MutableLiveData<List<FileModel.AudioFile>>()

    fun getFolderContent(path: String) {
        val files = repo.getPathContent(path).toFileModels()
        if (files.isEmpty()) {
            _emptyFolderLayoutVisibility.postValue(View.VISIBLE)
        } else {
            _emptyFolderLayoutVisibility.postValue(View.INVISIBLE)
        }
//        if (repo.containsAudioFiles(path)) {
//            _bottomBarVisibility.postValue(View.VISIBLE)
//        } else _bottomBarVisibility.postValue(View.INVISIBLE)
        _currentFiles.postValue(files)

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

    fun onSubmitButtonClicked(v: View) {

    }
}
