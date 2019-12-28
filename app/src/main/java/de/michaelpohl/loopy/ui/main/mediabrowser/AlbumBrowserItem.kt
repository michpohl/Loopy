package de.michaelpohl.loopy.ui.main.mediabrowser

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
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