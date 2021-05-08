package de.michaelpohl.loopy.ui.filebrowser.adapter

import android.view.ViewGroup
import com.example.adapter.adapter.AdapterItemDelegate
import com.example.adapter.adapter.inflateLayout
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel

class FileItemDelegate :
    AdapterItemDelegate<FileModel.File, FileItemHolder>() {

    override fun createViewHolder(parent: ViewGroup): FileItemHolder {
        return FileItemHolder(inflateLayout(R.layout.item_browser, parent))
    }

    override fun isForItemType(item: Any): Boolean {
        return item is FileModel.File
    }
}
