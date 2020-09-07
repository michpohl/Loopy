package de.michaelpohl.loopy.ui.main.mediastorebrowser.adapter

import android.view.ViewGroup
import com.example.adapter.adapter.AdapterItemDelegate
import com.example.adapter.adapter.util.inflateLayout
import de.michaelpohl.loopy.R

class TrackDelegate(private val receiver: (MediaStoreItemModel.Track, Boolean) -> Unit) : AdapterItemDelegate<MediaStoreItemModel.Track, TrackItemHolder>() {
    override fun createViewHolder(parent: ViewGroup): TrackItemHolder {
        val holder =  TrackItemHolder(inflateLayout(R.layout.item_browser, parent))
        holder.onCheckedChangedReceiver = this.receiver
        return holder
    }

    override fun isForItemType(item: Any): Boolean {
        return item is MediaStoreItemModel.Track
    }
}
