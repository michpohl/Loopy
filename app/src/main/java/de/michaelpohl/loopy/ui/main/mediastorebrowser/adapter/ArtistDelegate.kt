package de.michaelpohl.loopy.ui.main.mediastorebrowser.adapter

import android.view.ViewGroup
import com.example.adapter.adapter.AdapterItemDelegate
import com.example.adapter.adapter.util.inflateLayout
import de.michaelpohl.loopy.R

class ArtistDelegate : AdapterItemDelegate<MediaStoreItemModel.Artist, ArtistItemHolder>() {
    override fun createViewHolder(parent: ViewGroup): ArtistItemHolder {
        return ArtistItemHolder(inflateLayout(R.layout.item_browser, parent))
    }

    override fun isForItemType(item: Any): Boolean {
        return item is MediaStoreItemModel.Artist
    }
}
