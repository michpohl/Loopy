package com.michaelpohl.loopyplayer2.ui.filebrowser.adapter

import android.view.View
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.common.getDrawable
import com.michaelpohl.loopyplayer2.common.gone
import com.michaelpohl.shared.FileModel

class FolderItemHolder(itemView: View) : BrowserItemHolder<FileModel.Folder>(itemView) {

    override fun bind(item: FileModel.Folder) {
        label.text = item.name
        subLabel.text = resources.getString(R.string.folder_item_content_info, item.audioSubFiles.toString())
        icon.setImageDrawable(getDrawable(R.drawable.ic_folder))
        checkBox.gone()
    }
}
