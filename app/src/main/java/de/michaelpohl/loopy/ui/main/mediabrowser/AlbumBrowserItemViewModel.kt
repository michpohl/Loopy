package de.michaelpohl.loopy.ui.main.mediabrowser

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

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
