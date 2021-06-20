package com.michaelpohl.loopyplayer2.ui.mediastorebrowser.adapter

import android.view.ViewGroup
import com.michaelpohl.delegationadapter.AdapterItemDelegate
import com.michaelpohl.delegationadapter.inflateLayout
import com.michaelpohl.loopyplayer2.R

class TrackDelegate(private val receiver: (MediaStoreItemModel.Track) -> Unit) :
    AdapterItemDelegate<MediaStoreItemModel.Track, TrackItemHolder>() {

    override fun createViewHolder(parent: ViewGroup): TrackItemHolder {
        val holder = TrackItemHolder(inflateLayout(R.layout.item_browser, parent))
        holder.onCheckedChangedReceiver = this.receiver
        return holder
    }

    override fun isForItemType(item: Any): Boolean {
        return item is MediaStoreItemModel.Track
    }
}
