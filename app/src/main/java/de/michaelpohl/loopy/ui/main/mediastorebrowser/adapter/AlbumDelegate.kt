package de.michaelpohl.loopy.ui.main.mediastorebrowser.adapter

import android.view.ViewGroup
import com.example.adapter.adapter.ClickableAdapterItemDelegate
import com.example.adapter.adapter.util.inflateLayout
import de.michaelpohl.loopy.R

class AlbumDelegate(override val receiver: ((MediaStoreItemModel.Album) -> Unit)) : ClickableAdapterItemDelegate<MediaStoreItemModel.Album, AlbumItemHolder>() {
    override fun createViewHolder(parent: ViewGroup): AlbumItemHolder {
        return AlbumItemHolder(inflateLayout(R.layout.item_browser, parent))
    }

    override fun isForItemType(item: Any): Boolean {
        return item is MediaStoreItemModel.Album
    }
}
