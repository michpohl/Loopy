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

    var bottomBarVisibility = MediatorLiveData<Int>()

    private var _emptyFolderLayoutVisibility =
        MutableLiveData(View.INVISIBLE) //override if interested
    var emptyFolderLayoutVisibility = _emptyFolderLayoutVisibility.immutable()

    private var _selectButtonText = MutableLiveData(getString(R.string.btn_select_all))
    var selectButtonText = _selectButtonText.immutable()

    init {
        _entriesToDisplay.value =
            getMediaStoreEntries { it.filterIsInstance<MediaStoreItemModel.Album>() }
    }


    fun getMediaStoreEntries(filterBy: ((List<MediaStoreItemModel>) -> List<MediaStoreItemModel>)? = null): List<MediaStoreItemModel> {
        filterBy?.let { filterMethod ->
            return filterMethod(mediaStoreEntries)
        } ?: return mediaStoreEntries
    }

    fun onOpenSelectionClicked(view: View) {

    }

}
