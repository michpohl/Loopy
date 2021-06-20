package com.michaelpohl.loopyplayer2.ui.mediastorebrowser

import com.michaelpohl.loopyplayer2.common.*
import com.michaelpohl.loopyplayer2.model.AppStateRepository
import com.michaelpohl.loopyplayer2.ui.filebrowser.BrowserViewModel
import com.michaelpohl.loopyplayer2.ui.mediastorebrowser.adapter.MediaStoreItemModel
import timber.log.Timber
import java.io.File

class MediaStoreBrowserViewModel(
    private val repo: MediaStoreRepository,
    val appStateRepository: AppStateRepository
) : BrowserViewModel<MediaStoreBrowserViewModel.UIState>() {

    override fun onFragmentResumed() {
        super.onFragmentResumed()
        _state.value = initUIState()
    }

    data class UIState(
        val currentPath: String? = null,
        val acceptedTypes: Set<AppStateRepository.Companion.AudioFileType>,
        override val itemsToDisplay: List<MediaStoreItemModel>,
        override val lastDisplayedItems: List<List<MediaStoreItemModel>>? = listOf(),
        override val selectedItems: List<MediaStoreItemModel.Track>? = listOf(),
    ) : BrowserViewModel.BrowserUIState() {

        override val shouldShowEmptyMessage = itemsToDisplay.isEmpty().toVisibility()
        override val shouldShowSubmitButton = (selectedItems?.isNotEmpty() ?: false).toVisibility()
        override val shouldShowSelectAllButton =
            (itemsToDisplay.filterIsInstance<MediaStoreItemModel.Track>().size > 1).toVisibility()
    }

    override fun initUIState(): UIState {
        return UIState(
            acceptedTypes = appStateRepository.settings.acceptedFileTypes.toSet(),
            itemsToDisplay = filterAllAlbums()
        )
    }

    fun onSubmitClicked() {
        val audioModels = currentState.selectedItems.orEmpty().map {
            val file = File(it.path)
            file.toFileModel(currentState.acceptedTypes)
        }
        onSelectionSubmittedListener(audioModels.filterIsInstance<FileModel.AudioFile>())
    }

    override fun selectAll() {
        Timber.d("selectall")
        _state.value = currentState.copy(
            selectedItems = currentState.itemsToDisplay.filterIsInstance<MediaStoreItemModel.Track>()
        )
    }

//    fun onArtistClicked(artist: MediaStoreItemModel.Artist) {
//        _entriesToDisplay.postValue(filterAllTracksFromArtist(artist))
//    }

    fun onAlbumClicked(album: MediaStoreItemModel.Album) {
        val backList = currentState.lastDisplayedItems.orEmpty().toMutableList()
        backList.add(currentState.itemsToDisplay)
        _state.value = currentState.copy(
            itemsToDisplay = filterAllTracksFromAlbum(album),
            lastDisplayedItems = backList
        )
    }

    private fun filterAllAlbums() = repo.getMediaStoreEntries().filterIsInstance<MediaStoreItemModel.Album>()
    private fun filterAllTracksFromAlbum(album: MediaStoreItemModel.Album): List<MediaStoreItemModel.Track> {
        return repo.getMediaStoreEntries()
            .filterIsInstance<MediaStoreItemModel.Track>()
            .filter { it.album == album.name }
            .filter { it.path.hasAcceptedAudioFileExtension(currentState.acceptedTypes) }
    }

//    private fun filterAllTracksFromArtist(artist: MediaStoreItemModel.Artist): List<MediaStoreItemModel.Track> {
//        return mediaStoreEntries
//            .filterIsInstance<MediaStoreItemModel.Track>()
//            .filter { it.artist == artist.name }
//    }

    fun onBackPressed(): Boolean {
        val lastDisplayedItems = currentState.lastDisplayedItems.orEmpty().toMutableList()

        with(lastDisplayedItems) {
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

    fun onTrackSelectionChanged(track: MediaStoreItemModel.Track) {
        Timber.d("Clicked on: ${track.path}, is  selected: ${track.isSelected}")
        val currentList = currentState.selectedItems.orEmpty().toMutableList()
        if (track.isSelected == true && currentList.find { it.path == track.path } == null) {
            currentList.add(track)
        } else {
            currentList.remove(currentList.find { it.path == track.path })
        }
        _state.value = currentState.copy(selectedItems = currentList)
    }
}
