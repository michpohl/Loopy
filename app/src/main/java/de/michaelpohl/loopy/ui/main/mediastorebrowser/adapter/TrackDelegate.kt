package de.michaelpohl.loopy.ui.main.mediastorebrowser.adapter

import android.view.ViewGroup
import com.example.adapter.adapter.AdapterItemDelegate
import com.example.adapter.adapter.util.inflateLayout
import de.michaelpohl.loopy.R

class TrackDelegate : AdapterItemDelegate<MediaStoreItemModel.Track, TrackItemHolder>() {
    override fun createViewHolder(parent: ViewGroup): TrackItemHolder {
        return TrackItemHolder(inflateLayout(R.layout.item_browser, parent))
    }

    override fun isForItemType(item: Any): Boolean {
        return item is MediaStoreItemModel.Track
    }
}
