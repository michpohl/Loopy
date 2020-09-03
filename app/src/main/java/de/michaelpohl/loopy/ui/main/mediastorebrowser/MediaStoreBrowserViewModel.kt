package de.michaelpohl.loopy.ui.main.mediastorebrowser

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.*
import de.michaelpohl.loopy.ui.main.BaseViewModel
import de.michaelpohl.loopy.ui.main.filebrowser.FileBrowserViewModel
import de.michaelpohl.loopy.ui.main.mediastorebrowser.adapter.MediaStoreItemModel

open class MediaStoreBrowserViewModel(private val repo: MediaStoreRepository) : BaseViewModel() {

    lateinit var onSelectionSubmittedListener: (List<MediaStoreItemModel.Track>) -> Unit

    // TODO this should be a list of MediaStoreItem
    private val mediaStoreEntries = MutableLiveData(repo.getMediaStoreEntries())

    var bottomBarVisibility = MediatorLiveData<Int>()


    private var _emptyFolderLayoutVisibility =
        MutableLiveData(View.INVISIBLE) //override if interested
    var emptyFolderLayoutVisibility = _emptyFolderLayoutVisibility.immutable()

    private var _selectButtonText = MutableLiveData(getString(R.string.btn_select_all))
    var selectButtonText = _selectButtonText.immutable()


    fun getMediaStoreEntries(filterBy: ((List<MediaStoreItemModel>) -> List<MediaStoreItemModel>)? = null) : LiveData<List<MediaStoreItemModel>> {
        // TODO implement the whole filtering stuff
        val payload = mediaStoreEntries.value.orEmpty().map { MediaStoreItemModel.Album(it.name) }
        return MutableLiveData<List<MediaStoreItemModel>>(payload).immutable()
    }

    fun onOpenSelectionClicked(view: View) {

    }

}
