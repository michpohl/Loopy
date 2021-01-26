package de.michaelpohl.loopy.ui.filebrowser.adapter

import android.view.View
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.gone

class FileItemHolder(itemView: View) : BrowserItemHolder<FileModel.File>(itemView) {

    override fun bind(item: FileModel.File) {
        label.text = item.name
        checkBox.gone()
    }
}
