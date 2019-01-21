package de.michaelpohl.loopy.ui.main.mediabrowser

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.databinding.ItemAlbumBrowserBinding

class AlbumBrowserAdapter(
    private val onItemClickedListener: ((String) -> Unit)
) : RecyclerView.Adapter<AlbumBrowserItem>() {

    private var albums = listOf<String>()
        set (newList) {
            //sort them a-z
            field = newList.sortedWith(compareBy { it })
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumBrowserItem {

        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemAlbumBrowserBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_album_browser, parent, false)
        return AlbumBrowserItem(
            parent.context,
            binding
        )
    }

    override fun getItemCount() = albums.size

    override fun onBindViewHolder(holder: AlbumBrowserItem, position: Int) {

        val albumViewModel = AlbumBrowserItemViewModel(
            position,
            albums[position],
            this::onItemClicked
        )

        holder.bind(albumViewModel)
        albumViewModel.update()
    }

    fun updateData(albums: List<String>) {
        this.albums = albums
        notifyDataSetChanged()
    }

    private fun onItemClicked(album: String) {
        onItemClickedListener.invoke(album)
    }
}