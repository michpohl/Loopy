package de.michaelpohl.loopy.ui.main.filebrowser

import android.view.View
import de.michaelpohl.loopy.common.FileModel

class FileItemHolder(itemView: View) : ItemHolder<FileModel.File>(itemView) {

    override fun bind(item: FileModel.File) {
        with(item) {
            label.text = name
        }
    }

}
