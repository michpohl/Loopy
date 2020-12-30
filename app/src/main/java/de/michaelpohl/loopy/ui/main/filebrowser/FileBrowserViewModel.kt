package de.michaelpohl.loopy.ui.main.filebrowser

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.StorageRepository
import de.michaelpohl.loopy.common.toFileModels
import de.michaelpohl.loopy.common.toVisibility
import de.michaelpohl.loopy.model.AppStateRepository
import de.michaelpohl.loopy.ui.main.base.BaseUIState
import timber.log.Timber

open class FileBrowserViewModel(
    private val storage: StorageRepository,
    val appStateRepository: AppStateRepository
) :
    BrowserViewModel<FileBrowserViewModel.UIState>() {

    var initialPath: String? = null
        set(value) {
            field = value
            value?.let {
                _state.value = (currentState.copy(
                    currentPath = it,
                    filesToDisplay = getFolderContent(it)
                ))
            }
        }

    override val selectedFiles = MutableLiveData<List<FileModel.AudioFile>>() // TODO remove
    override fun initUIState(): UIState {
        return UIState(
            acceptedTypes = appStateRepository.settings.acceptedFileTypes.toSet(),
            filesToDisplay = listOf()
        )
    }

    data class UIState(
        val currentPath: String? = null,
        val acceptedTypes: Set<AppStateRepository.Companion.AudioFileType>,
        val filesToDisplay: List<FileModel>,
        val lastDisplayedFiles: List<List<FileModel>>? = listOf(),
        val selectedFiles: List<FileModel.AudioFile>? = listOf(),
    ) : BaseUIState() {

        val shouldShowEmptyMessage = filesToDisplay.isEmpty().toVisibility()
        val shouldShowSubmitButton = (selectedFiles?.isNotEmpty() ?: false).toVisibility()

        val shouldShowSelectAllButton = (filesToDisplay.filterIsInstance<FileModel.AudioFile>().size > 1).toVisibility()

        init {
            Timber.d("Should: $shouldShowEmptyMessage, $shouldShowSelectAllButton, $shouldShowSubmitButton")
        }
    }

    fun getFolderContent(path: String): List<FileModel> {
        return storage.getPathContent(path).toFileModels(currentState.acceptedTypes)
    }

    fun onFolderClicked(folder: FileModel.Folder) {
        // we're keeping the items just displayed so the back button can work properly
        val backList = currentState.lastDisplayedFiles.orEmpty().toMutableList()
        backList.add(currentState.filesToDisplay)
        _state.postValue(
            currentState.copy(
                filesToDisplay = getFolderContent(folder.path),
                lastDisplayedFiles = backList
            ))
    }

    fun onFileSelectionChanged(fileModel: FileModel.AudioFile) {
        Timber.d("Selected: $fileModel")
        val currentList = currentState.selectedFiles.orEmpty().toMutableList()
        if (fileModel.isSelected == true && currentList.find { it.path == fileModel.path} == null) {
            currentList.add(fileModel)
        } else {
            currentList.remove(currentList.find { it.path == fileModel.path})
        }
        _state.value = currentState.copy(selectedFiles = currentList)
    }

    fun onSubmitClicked() {
        onSelectionSubmittedListener(currentState.selectedFiles.orEmpty())
    }

    override fun selectAll() {
        _state.value = currentState.copy(selectedFiles = currentState.filesToDisplay.filterIsInstance<FileModel.AudioFile>())
    }

    fun onBackPressed(): Boolean {
        val lastDisplayedFiles = currentState.lastDisplayedFiles.orEmpty().toMutableList()

        with(lastDisplayedFiles) {
            return if (this.isNotEmpty()) {
                val nextFilesToDisplay = this.last()
                remove(this.last())
                _state.postValue(
                    currentState.copy(
                        filesToDisplay = nextFilesToDisplay,
                        lastDisplayedFiles = this,
                        selectedFiles = null
                    ))
                true
            } else false
        }
    }
}
