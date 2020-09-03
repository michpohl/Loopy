package de.michaelpohl.loopy.ui.main.filebrowser.adapter

import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.getDrawable
import de.michaelpohl.loopy.common.gone

class FolderItemHolder(itemView: View) : BrowserItemHolder<FileModel.Folder>(itemView) {
    override fun bind(item: FileModel.Folder) {
        label.text = item.name
        subLabel.text = "(${item.subFiles} files)" //TODO change to resource
        icon.setImageDrawable(getDrawable(R.drawable.ic_folder))
        checkBox.gone()
    }
}
