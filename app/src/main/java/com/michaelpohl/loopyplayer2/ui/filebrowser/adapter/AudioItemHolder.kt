package com.michaelpohl.loopyplayer2.ui.filebrowser.adapter

import android.view.View
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.common.FileModel
import com.michaelpohl.loopyplayer2.common.getDrawable
import com.michaelpohl.loopyplayer2.common.roundTo
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
