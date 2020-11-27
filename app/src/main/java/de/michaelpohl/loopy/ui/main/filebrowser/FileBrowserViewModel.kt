package de.michaelpohl.loopy.ui.main.filebrowser

import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.StorageRepository
import de.michaelpohl.loopy.common.immutable
import de.michaelpohl.loopy.common.toFileModels
import de.michaelpohl.loopy.ui.main.base.BaseUIState
import de.michaelpohl.loopy.ui.main.base.BaseViewModel

open class FileBrowserViewModel(private val repo: StorageRepository) :
    BrowserViewModel() {

    private val _filesToDisplay = MutableLiveData<List<FileModel>>()
    val filesToDisplay = _filesToDisplay.immutable()

    private val lastDisplayedFiles = mutableListOf<List<FileModel>>()

    // TODO this doesn't seem to be properly connected yet
    var bottomBarVisibility = MediatorLiveData<Int>()


    private var _emptyFolderLayoutVisibility =
        MutableLiveData(View.INVISIBLE) //override if interested
    var emptyFolderLayoutVisibility = _emptyFolderLayoutVisibility.immutable()

    private var _selectButtonText = MutableLiveData(getString(R.string.btn_select_all))
    var selectButtonText = _selectButtonText.immutable()

    override val selectedFiles = MutableLiveData<List<FileModel.AudioFile>>()

    override fun initUIState(): BaseUIState {
        // TODO refactor
        return object : BaseUIState() {}
    }

    fun getFolderContent(path: String) {
        val files = repo.getPathContent(path).toFileModels()
        if (files.isEmpty()) {
            _emptyFolderLayoutVisibility.postValue(View.VISIBLE)
        } else {
            _emptyFolderLayoutVisibility.postValue(View.INVISIBLE)
        }
        _filesToDisplay.postValue(files)

    }

    fun onFolderClicked(folder: FileModel.Folder) {
        // keeping the items just diplayed so the backbutton can work properly
        lastDisplayedFiles.add(filesToDisplay.value.orEmpty())
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
    }

    fun onSubmitClicked() {
        onSelectionSubmittedListener(selectedFiles.value.orEmpty())
//        submitSelection(selectedFiles.value.orEmpty())
    }

    override fun selectAll() {
        TODO("Not yet implemented")
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
