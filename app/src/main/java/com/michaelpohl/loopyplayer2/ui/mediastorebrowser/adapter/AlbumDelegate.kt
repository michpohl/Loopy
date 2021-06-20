package com.michaelpohl.loopyplayer2.ui.mediastorebrowser.adapter

import android.view.ViewGroup
import com.michaelpohl.delegationadapter.ClickableAdapterItemDelegate
import com.michaelpohl.delegationadapter.inflateLayout
import com.michaelpohl.loopyplayer2.R

class AlbumDelegate(override val clickListener: ((MediaStoreItemModel.Album) -> Unit)) :
    ClickableAdapterItemDelegate<MediaStoreItemModel.Album, AlbumItemHolder>() {

    override fun createViewHolder(parent: ViewGroup): AlbumItemHolder {
        return AlbumItemHolder(inflateLayout(R.layout.item_browser, parent))
    }

    override fun isForItemType(item: Any): Boolean {
        return item is MediaStoreItemModel.Album
    }
}
