package de.michaelpohl.loopy.ui.main.media_browser

import android.content.Context
import android.support.v7.widget.RecyclerView
import de.michaelpohl.loopy.databinding.ItemAlbumBrowserBinding

class AlbumBrowserItem(
    val context: Context,
    var binding: ItemAlbumBrowserBinding

) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: AlbumBrowserItemViewModel) {
        binding.model = model
        binding.executePendingBindings()
    }
}