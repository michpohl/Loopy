package de.michaelpohl.loopy.ui.main.filebrowser

import android.view.ViewGroup
import com.deutschebahn.streckenagent2.ui.common.recycler.ClickableAdapterItemDelegate
import com.example.adapter.adapter.util.inflateLayout
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel

class FileItemDelegate(override val receiver: (FileModel) -> Unit) :
    ClickableAdapterItemDelegate<FileModel.File, FileModel, FileItemHolder>() {
    override fun createViewHolder(parent: ViewGroup): FileItemHolder {
        return FileItemHolder(inflateLayout(R.layout.item_browser, parent))
    }

    override fun isForItemType(item: Any): Boolean {
        return item is FileModel.File
    }
}
