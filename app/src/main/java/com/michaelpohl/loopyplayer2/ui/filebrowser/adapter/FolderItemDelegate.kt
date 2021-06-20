package com.michaelpohl.loopyplayer2.ui.filebrowser.adapter

import android.view.ViewGroup
import com.michaelpohl.delegationadapter.ClickableAdapterItemDelegate
import com.michaelpohl.delegationadapter.inflateLayout
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.common.FileModel

class FolderItemDelegate(override val clickListener: (FileModel.Folder) -> Unit) :
    ClickableAdapterItemDelegate<FileModel.Folder, FolderItemHolder>() {

    override fun createViewHolder(parent: ViewGroup): FolderItemHolder {
        return FolderItemHolder(inflateLayout(R.layout.item_browser, parent))
    }

    override fun isForItemType(item: Any): Boolean {
        return item is FileModel.Folder
    }
}
