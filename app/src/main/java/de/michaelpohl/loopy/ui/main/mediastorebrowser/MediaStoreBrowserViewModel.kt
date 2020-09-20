package de.michaelpohl.loopy.ui.main.mediastorebrowser

import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.MediaStoreRepository
import de.michaelpohl.loopy.common.immutable
import de.michaelpohl.loopy.common.toFileModel
import de.michaelpohl.loopy.ui.main.base.BaseViewModel
import de.michaelpohl.loopy.ui.main.mediastorebrowser.adapter.MediaStoreItemModel
import timber.log.Timber
import java.io.File

open class MediaStoreBrowserViewModel(private val repo: MediaStoreRepository) : BaseViewModel() {

    private val mediaStoreEntries = repo.getMediaStoreEntries()
    private val _entriesToDisplay = MutableLiveData<List<MediaStoreItemModel>>()
    private val lastDisplayedEntries = mutableListOf<List<MediaStoreItemModel>>()
    private val selectedFiles = MutableLiveData<List<MediaStoreItemModel.Track>>()

    val entriesToDisplay = _entriesToDisplay.immutable()

    private var _emptyFolderLayoutVisibility = MutableLiveData(View.INVISIBLE)
    private var _selectButtonText = MutableLiveData(getString(R.string.btn_select_all))

    var emptyFolderLayoutVisibility = _emptyFolderLayoutVisibility.immutable()
    var selectButtonText = _selectButtonText.immutable()
    var bottomBarVisibility = MediatorLiveData<Int>()

    lateinit var onSelectionSubmittedListener: (List<FileModel.AudioFile>) -> Unit

    init {
        // initially, we want to show a list of all Albums
        _entriesToDisplay.value = filterAllAlbums()
    }

    fun onOpenSelectionClicked(view: View) {
        val audioModels = selectedFiles.value.orEmpty().map {
            val file = File(it.path)
            file.toFileModel()
        }
        onSelectionSubmittedListener(audioModels.filterIsInstance<FileModel.AudioFile>())
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
        if (isSelected) {
            currentList.add(track)
        } else {
            currentList.remove(track)
        }
        selectedFiles.postValue(currentList)
        Timber.d("Currently selected: ${currentList.map { it.name }}")
    }

}
