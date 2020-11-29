package de.michaelpohl.loopy.ui.main.mediastorebrowser.adapter

import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.getDrawable

class TrackItemHolder(itemView: View) : MediaStoreItemHolder<MediaStoreItemModel.Track>(itemView) {
    lateinit var onCheckedChangedReceiver: (MediaStoreItemModel.Track, Boolean) -> Unit
    override fun bind(item: MediaStoreItemModel.Track) {
        label.text = item.name
        subLabel.text = item.album // TODO improve
        icon.setImageDrawable(getDrawable(R.drawable.ic_audio))
        itemView.setOnClickListener { checkBox.isChecked = !checkBox.isChecked }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChangedReceiver.invoke(
                item,
                isChecked
            )
        }
    }
}
