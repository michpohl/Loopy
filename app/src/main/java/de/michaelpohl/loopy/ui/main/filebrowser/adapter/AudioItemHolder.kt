package de.michaelpohl.loopy.ui.main.filebrowser.adapter

import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.getDrawable
import de.michaelpohl.loopy.common.roundTo

class AudioItemHolder(itemView: View) : ItemHolder<FileModel.AudioFile>(itemView) {

    lateinit var item: FileModel.File
    lateinit var onCheckedChangedReceiver: (FileModel.AudioFile, Boolean) -> Unit

    override fun bind(item: FileModel.AudioFile) {
        label.text = item.name
        subLabel.text = "${item.sizeInMB.roundTo(2)} MB" //TODO turn into string resource
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
