package com.michaelpohl.loopyplayer2.ui.filebrowser.adapter

import android.view.ViewGroup
import com.michaelpohl.delegationadapter.AdapterItemDelegate
import com.michaelpohl.delegationadapter.inflateLayout
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.common.FileModel

class FileItemDelegate :
    AdapterItemDelegate<FileModel.File, FileItemHolder>() {

    override fun createViewHolder(parent: ViewGroup): FileItemHolder {
        return FileItemHolder(inflateLayout(R.layout.item_browser, parent))
    }

    override fun isForItemType(item: Any): Boolean {
        return item is FileModel.File
    }
}
