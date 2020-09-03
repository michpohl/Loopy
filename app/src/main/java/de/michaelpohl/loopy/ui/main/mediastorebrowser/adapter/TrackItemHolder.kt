package de.michaelpohl.loopy.ui.main.mediastorebrowser.adapter

import android.view.View

class TrackItemHolder(itemView: View) : MediaStoreItemHolder<MediaStoreItemModel.Track>(itemView) {
    override fun bind(item: MediaStoreItemModel.Track) {
        label.text = item.name
    }

}
