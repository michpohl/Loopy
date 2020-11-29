package de.michaelpohl.loopy.ui.main.mediastorebrowser

import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.MediaStoreRepository
import de.michaelpohl.loopy.common.immutable
import de.michaelpohl.loopy.model.AppStateRepository
import de.michaelpohl.loopy.ui.main.base.BaseUIState
import de.michaelpohl.loopy.ui.main.filebrowser.BrowserViewModel
import de.michaelpohl.loopy.ui.main.mediastorebrowser.adapter.MediaStoreItemModel
import timber.log.Timber
import java.io.File

open class MediaStoreBrowserViewModel(private val repo: MediaStoreRepository, private val appStateRepository: AppStateRepository) : BrowserViewModel(appStateRepository) {

    private val mediaStoreEntries = repo.getMediaStoreEntries()
    private val _entriesToDisplay = MutableLiveData<List<MediaStoreItemModel>>()
    private val lastDisplayedEntries = mutableListOf<List<MediaStoreItemModel>>()
    override val selectedFiles = MutableLiveData<List<FileModel.AudioFile>>()

    val entriesToDisplay = _entriesToDisplay.immutable()

    private var _emptyFolderLayoutVisibility = MutableLiveData(View.INVISIBLE)
    private var _selectButtonText = MutableLiveData(getString(R.string.btn_select_all))

    var emptyFolderLayoutVisibility = _emptyFolderLayoutVisibility.immutable()
    var selectButtonText = _selectButtonText.immutable()
    var bottomBarVisibility = MediatorLiveData<Int>()

    init {
        // initially, we want to show a list of all Albums
        _entriesToDisplay.value = filterAllAlbums()
    }

    override fun initUIState(): BaseUIState {
        // TODO refactor
        return object : BaseUIState() {}
    }

    fun onSubmitClicked() {
        val audioModels = selectedFiles.value.orEmpty().map {
            val file = File(it.path)
            file.toFileModel()
        }
//        submitSelection(audioModels.filterIsInstance<FileModel.AudioFile>())
        onSelectionSubmittedListener(audioModels.filterIsInstance<FileModel.AudioFile>())
    }

    override fun selectAll() {
        TODO("Not yet implemented")
    }

    fun onArtistClicked(artist: MediaStoreItemModel.Artist) {
        _entriesToDisplay.postValue(filterAllTracksFromArtist(artist))
    }

    fun onAlbumClicked(album: MediaStoreItemModel.Album) {
        lastDisplayedEntries.add(entriesToDisplay.value.orEmpty())
        _entriesToDisplay.postValue(filterAllTracksFromAlbum(album))
    }

    private fun filterAllAlbums() = mediaStoreEntries.filterIsInstance<MediaStoreItemModel.Album>()

    private fun filterAllTracksFromAlbum(album: MediaStoreItemModel.Album): List<MediaStoreItemModel.Track> {
        return mediaStoreEntries
            .filterIsInstance<MediaStoreItemModel.Track>()
            .filter { it.album == album.name }
    }

    private fun filterAllTracksFromArtist(artist: MediaStoreItemModel.Artist): List<MediaStoreItemModel.Track> {
        return mediaStoreEntries
            .filterIsInstance<MediaStoreItemModel.Track>()
            .filter { it.artist == artist.name }
    }

    fun onBackPressed(): Boolean {
        with(lastDisplayedEntries) {
            return if (this.isNotEmpty()) {
                _entriesToDisplay.postValue(this.last())
                remove(this.last())
                true
            } else false

        }
    }

    fun onTrackSelectionChanged(track: MediaStoreItemModel.Track, isSelected: Boolean) {
        val currentList = selectedFiles.value.orEmpty().toMutableList()
            val file = File(track.path).toFileModel()
        if (isSelected) {
            if (file is FileModel.AudioFile) currentList.add(file)
        } else {
            currentList.remove(file)
        }
        selectedFiles.postValue(currentList)
        Timber.d("Currently selected: ${currentList.map { it.name }}")
    }

}
