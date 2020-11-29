package de.michaelpohl.loopy.ui.main.filebrowser.adapter

import android.view.ViewGroup
import com.example.adapter.adapter.ClickableAdapterItemDelegate
import com.example.adapter.adapter.inflateLayout
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel

class FolderItemDelegate(override val clickListener: (FileModel.Folder) -> Unit) :
    ClickableAdapterItemDelegate<FileModel.Folder, FolderItemHolder>() {

    override fun createViewHolder(parent: ViewGroup): FolderItemHolder {
        return FolderItemHolder(inflateLayout(R.layout.item_browser, parent))
    }

    override fun isForItemType(item: Any): Boolean {
        return item is FileModel.Folder
    }
}
