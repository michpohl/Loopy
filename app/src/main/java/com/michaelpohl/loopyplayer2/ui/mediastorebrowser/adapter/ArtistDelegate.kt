package com.michaelpohl.loopyplayer2.ui.mediastorebrowser.adapter

import android.view.ViewGroup
import com.michaelpohl.delegationadapter.AdapterItemDelegate
import com.michaelpohl.delegationadapter.inflateLayout
import com.michaelpohl.loopyplayer2.R

class ArtistDelegate : AdapterItemDelegate<MediaStoreItemModel.Artist, ArtistItemHolder>() {
    override fun createViewHolder(parent: ViewGroup): ArtistItemHolder {
        return ArtistItemHolder(inflateLayout(R.layout.item_browser, parent))
    }

    override fun isForItemType(item: Any): Boolean {
        return item is MediaStoreItemModel.Artist
    }
}
