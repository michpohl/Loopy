package de.michaelpohl.loopy.ui.main.filebrowser

import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.getDrawable
import de.michaelpohl.loopy.common.roundTo

class AudioItemHolder(itemView: View) : ItemHolder<FileModel.AudioFile>(itemView) {
    override fun bind(item: FileModel.AudioFile) {
        label.text = item.name
        subLabel.text = "${item.sizeInMB.roundTo(2)} MB" //TODO turn into string resource
        icon.setImageDrawable(getDrawable(R.drawable.ic_audio))
    }

}
