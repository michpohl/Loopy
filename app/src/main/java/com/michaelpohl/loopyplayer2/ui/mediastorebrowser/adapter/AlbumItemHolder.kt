package com.michaelpohl.loopyplayer2.ui.mediastorebrowser.adapter

import android.view.View
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.common.getDrawable
import com.michaelpohl.loopyplayer2.common.gone

class AlbumItemHolder(itemView: View) : MediaStoreItemHolder<MediaStoreItemModel.Album>(itemView) {
    override fun bind(item: MediaStoreItemModel.Album) {
        label.text = item.name
        subLabel.gone()
        icon.setImageDrawable(getDrawable(R.drawable.ic_album))
        checkBox.gone()
    }
}
