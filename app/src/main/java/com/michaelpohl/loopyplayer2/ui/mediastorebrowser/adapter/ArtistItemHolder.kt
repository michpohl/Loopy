package com.michaelpohl.loopyplayer2.ui.mediastorebrowser.adapter

import android.view.View
import com.michaelpohl.loopyplayer2.common.gone

class ArtistItemHolder(itemView: View) : MediaStoreItemHolder<MediaStoreItemModel.Artist>(itemView) {
    override fun bind(item: MediaStoreItemModel.Artist) {
        label.text = item.name
        checkBox.gone()
    }
}
