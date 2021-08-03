package com.michaelpohl.loopyplayer2.ui.filebrowser.adapter

import android.view.View
import com.michaelpohl.shared.FileModel
import com.michaelpohl.loopyplayer2.common.gone

class FileItemHolder(itemView: View) : BrowserItemHolder<FileModel.File>(itemView) {

    override fun bind(item: FileModel.File) {
        label.text = item.name
        checkBox.gone()
    }
}
