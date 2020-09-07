package de.michaelpohl.loopy.ui.main.mediastorebrowser.adapter

import android.view.View
import de.michaelpohl.loopy.common.gone

class ArtistItemHolder(itemView: View) : MediaStoreItemHolder<MediaStoreItemModel.Artist>(itemView) {
    override fun bind(item: MediaStoreItemModel.Artist) {
        label.text = item.name
        checkBox.gone()
    }

}
