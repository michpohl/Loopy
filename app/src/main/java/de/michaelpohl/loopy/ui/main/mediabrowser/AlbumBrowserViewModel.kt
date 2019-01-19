package de.michaelpohl.loopy.ui.main.browser

import android.app.Application
import de.michaelpohl.loopy.ui.main.mediabrowser.AlbumBrowserAdapter

class AlbumBrowserViewModel(application: Application) : BrowserViewModel(application) {

    private var adapter =
        AlbumBrowserAdapter(this::onItemClicked)

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