package de.michaelpohl.loopy.ui.main.media_browser

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.view.View

class AlbumBrowserItemViewModel(
    private val position: Int,  //TODO is this needed?
    private val album: String,

    private val onItemClickedListener: ((String) -> Unit)
) : ViewModel() {

    var name = ObservableField("name")

    fun update() {
        name.set(album)
    }

    fun onItemClicked(view: View) {
        onItemClickedListener.invoke(album)
    }
}
