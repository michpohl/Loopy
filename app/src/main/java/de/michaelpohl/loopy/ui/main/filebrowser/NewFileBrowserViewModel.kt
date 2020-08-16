package de.michaelpohl.loopy.ui.main.filebrowser

import android.opengl.Visibility
import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.*
import de.michaelpohl.loopy.model.DataRepository
import de.michaelpohl.loopy.ui.main.BaseViewModel
import timber.log.Timber

class NewFileBrowserViewModel(private val repo: StorageRepository) : BaseViewModel() {

    private val _currentFiles = MutableLiveData<List<FileModel>>()
    val currentFiles = _currentFiles.immutable()

    var bottomBarVisibility = MediatorLiveData<Int>()

    protected var _emptyFolderLayoutVisibility =
        MutableLiveData(View.INVISIBLE) //override if interested
    var emptyFolderLayoutVisibility = _emptyFolderLayoutVisibility.immutable()

    var _selectButtonText = MutableLiveData(getString(R.string.btn_select_all))
    var selectButtonText = _selectButtonText.immutable()

    private val selectedFiles = MutableLiveData<List<FileModel>>()

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

    fun onItemClicked(fileModel: FileModel) {
        Timber.d("Clicked on item")
    }
}
