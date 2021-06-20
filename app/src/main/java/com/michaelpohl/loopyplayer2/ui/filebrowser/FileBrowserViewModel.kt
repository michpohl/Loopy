package com.michaelpohl.loopyplayer2.ui.filebrowser

import com.michaelpohl.loopyplayer2.common.FileModel
import com.michaelpohl.loopyplayer2.common.StorageRepository
import com.michaelpohl.loopyplayer2.common.toFileModels
import com.michaelpohl.loopyplayer2.common.toVisibility
import com.michaelpohl.loopyplayer2.model.AppStateRepository

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
                    itemsToDisplay = getFolderContent(it)
                ))
            }
        }

    override fun initUIState(): UIState {
        return UIState(
            acceptedTypes = appStateRepository.settings.acceptedFileTypes.toSet(),
            itemsToDisplay = listOf()
        )
    }

    data class UIState(
        val currentPath: String? = null,
        val acceptedTypes: Set<AppStateRepository.Companion.AudioFileType>,
        override val itemsToDisplay: List<FileModel>,
        override val lastDisplayedItems: List<List<FileModel>>? = listOf(),
        override val selectedItems: List<FileModel.AudioFile>? = listOf(),
    ) : BrowserUIState() {

        override val shouldShowEmptyMessage = itemsToDisplay.isEmpty().toVisibility()
        override val shouldShowSubmitButton = (selectedItems?.isNotEmpty() ?: false).toVisibility()
        override val shouldShowSelectAllButton =
            (itemsToDisplay.filterIsInstance<FileModel.AudioFile>().size > 1).toVisibility()
    }

    fun getFolderContent(path: String): List<FileModel> {
        return storage.getPathContent(path).toFileModels(currentState.acceptedTypes)
    }

    fun onFolderClicked(folder: FileModel.Folder) {
        // we're keeping the items just displayed so the back button can work properly
        val backList = currentState.lastDisplayedItems.orEmpty().toMutableList()
        backList.add(currentState.itemsToDisplay)
        _state.postValue(
            currentState.copy(
                itemsToDisplay = getFolderContent(folder.path),
                lastDisplayedItems = backList
            ))
    }

    fun onFileSelectionChanged(fileModel: FileModel.AudioFile) {
        val currentList = currentState.selectedItems.orEmpty().toMutableList()
        if (fileModel.isSelected == true && currentList.find { it.path == fileModel.path } == null) {
            currentList.add(fileModel)
        } else {
            currentList.remove(currentList.find { it.path == fileModel.path })
        }
        _state.value = currentState.copy(selectedItems = currentList)
    }

    fun onSubmitClicked() {
        onSelectionSubmittedListener(currentState.selectedItems.orEmpty())
    }

    override fun selectAll() {
        _state.value = currentState.copy(selectedItems = currentState.itemsToDisplay.filterIsInstance<FileModel.AudioFile>())
    }

    fun onBackPressed(): Boolean {
        val lastDisplayedFiles = currentState.lastDisplayedItems.orEmpty().toMutableList()

        with(lastDisplayedFiles) {
            return if (this.isNotEmpty()) {
                val nextFilesToDisplay = this.last()
                remove(this.last())
                _state.postValue(
                    currentState.copy(
                        itemsToDisplay = nextFilesToDisplay,
                        lastDisplayedItems = this,
                        selectedItems = null
                    ))
                true
            } else false
        }
    }
}
