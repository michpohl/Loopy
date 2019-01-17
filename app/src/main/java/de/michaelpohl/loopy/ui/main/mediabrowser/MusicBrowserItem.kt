package de.michaelpohl.loopy.ui.main.mediabrowser

import android.content.Context
import android.support.v7.widget.RecyclerView
import de.michaelpohl.loopy.databinding.ItemFileBrowserBinding

class MusicBrowserItem(
    val context: Context,
    var binding: ItemFileBrowserBinding

) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: FileBrowserItemViewModel) {
        binding.model = model
        binding.executePendingBindings()
    }
}