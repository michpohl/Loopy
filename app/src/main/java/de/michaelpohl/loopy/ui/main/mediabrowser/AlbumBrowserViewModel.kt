package de.michaelpohl.loopy.ui.main.browser

import android.app.Application
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.view.View
import de.michaelpohl.loopy.ui.main.BaseViewModel
import de.michaelpohl.loopy.ui.main.mediabrowser.AlbumBrowserAdapter

class AlbumBrowserViewModel(application: Application) : BaseViewModel(application) {

    private var adapter =
        AlbumBrowserAdapter(this::onItemClicked)

    var emptyFolderLayoutVisibility = ObservableField<Int>(View.INVISIBLE)
    var bottomBarVisibility = ObservableInt(View.INVISIBLE)

    lateinit var listener: OnItemClickListener
    lateinit var albums: List<String>

    fun getAdapter(): AlbumBrowserAdapter {
        return adapter
    }

    fun updateAdapter() {

        adapter.updateData(albums)
    }

    private fun onItemClicked(albumTitle: String) {
        listener.onAlbumClicked(albumTitle)
    }

    interface OnItemClickListener {
        fun onAlbumClicked(albumTitle: String)
    }
}