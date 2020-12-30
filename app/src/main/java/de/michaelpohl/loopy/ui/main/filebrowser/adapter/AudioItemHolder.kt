package de.michaelpohl.loopy.ui.main.filebrowser.adapter

import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.getDrawable
import de.michaelpohl.loopy.common.roundTo
import timber.log.Timber

class AudioItemHolder(itemView: View) : BrowserItemHolder<FileModel.AudioFile>(itemView) {

    lateinit var onCheckedChangedReceiver: (FileModel.AudioFile) -> Unit
    override fun bind(item: FileModel.AudioFile) {
        label.text = item.name
        subLabel.text = "${item.sizeInMB.roundTo(2)} MB" //TODO turn into string resource
        icon.setImageDrawable(getDrawable(R.drawable.ic_audio))

        itemView.setOnClickListener { checkBox.isChecked = !checkBox.isChecked }

        checkBox.apply {
            isChecked = item.isSelected ?: false
            setOnCheckedChangeListener { _, isChecked ->
                Timber.d("Is Checked: $isChecked")
                if (isChecked != item.isSelected) {

                onCheckedChangedReceiver.invoke(
                    item.copy(isSelected = isChecked)
                )
                }
            }
        }
    }
}
