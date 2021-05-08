package de.michaelpohl.loopy.ui.mediastorebrowser.adapter

import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.getDrawable

class TrackItemHolder(itemView: View) : MediaStoreItemHolder<MediaStoreItemModel.Track>(itemView) {

    lateinit var onCheckedChangedReceiver: (MediaStoreItemModel.Track) -> Unit
    override fun bind(item: MediaStoreItemModel.Track) {
        label.text = item.name
        subLabel.text = item.album // TODO improve
        icon.setImageDrawable(getDrawable(R.drawable.ic_audio))
        itemView.setOnClickListener { checkBox.isChecked = !checkBox.isChecked }

        checkBox.apply {
            isChecked = item.isSelected ?: false
            setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != item.isSelected) {
                    onCheckedChangedReceiver.invoke(item.copy(isSelected = isChecked))
                }
            }
        }
    }
}
