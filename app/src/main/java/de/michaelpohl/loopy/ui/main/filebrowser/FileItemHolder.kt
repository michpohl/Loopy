package de.michaelpohl.loopy.ui.main.filebrowser

import android.view.View
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.gone

class FileItemHolder(itemView: View) : ItemHolder<FileModel.File>(itemView) {

    override fun bind(item: FileModel.File) {
        label.text = item.name
        checkBox.gone()
    }

}
