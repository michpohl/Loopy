package de.michaelpohl.loopy.ui.main.mediastorebrowser

import android.view.View
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.MediaStoreRepository
import de.michaelpohl.loopy.common.immutable
import de.michaelpohl.loopy.ui.main.BaseViewModel
import de.michaelpohl.loopy.ui.main.mediastorebrowser.adapter.MediaStoreItemModel

open class MediaStoreBrowserViewModel(private val repo: MediaStoreRepository) : BaseViewModel() {

    lateinit var onSelectionSubmittedListener: (List<MediaStoreItemModel.Track>) -> Unit

    private val mediaStoreEntries = repo.getMediaStoreEntries()

    private val _entriesToDisplay = MutableLiveData<List<MediaStoreItemModel>>()
    val entriesToDisplay = _entriesToDisplay.immutable()

    private val lastDisplayedEntries = mutableListOf<List<MediaStoreItemModel>>()

    var bottomBarVisibility = MediatorLiveData<Int>()

    private var _emptyFolderLayoutVisibility =
        MutableLiveData(View.INVISIBLE) //override if interested
    var emptyFolderLayoutVisibility = _emptyFolderLayoutVisibility.immutable()

    private var _selectButtonText = MutableLiveData(getString(R.string.btn_select_all))
    var selectButtonText = _selectButtonText.immutable()

    init {
        // initially, we want to show a list of all Albums
        _entriesToDisplay.value = filterAllAlbums()
    }

    fun onOpenSelectionClicked(view: View) {
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

}
