package de.michaelpohl.loopy.ui.main.mediastorebrowser.adapter

import android.view.View

class AlbumItemHolder(itemView: View) : MediaStoreItemHolder<MediaStoreItemModel.Album>(itemView) {
    override fun bind(item: MediaStoreItemModel.Album) {
        label.text = item.name
    }

}
