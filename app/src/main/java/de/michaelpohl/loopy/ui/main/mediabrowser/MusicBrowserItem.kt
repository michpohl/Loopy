package de.michaelpohl.loopy.ui.main.mediabrowser

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import de.michaelpohl.loopy.databinding.ItemMusicBrowserBinding

class MusicBrowserItem(
    val context: Context,
    var binding: ItemMusicBrowserBinding

) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: MusicBrowserItemViewModel) {
        binding.model = model
        binding.executePendingBindings()
    }
}