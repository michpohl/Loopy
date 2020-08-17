package de.michaelpohl.loopy.ui.main.filebrowser

import android.view.ViewGroup
import com.deutschebahn.streckenagent2.ui.common.recycler.ClickableAdapterItemDelegate
import com.example.adapter.adapter.util.inflateLayout
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel

class FolderItemDelegate(override val receiver: (FileModel.Folder) -> Unit) :
    ClickableAdapterItemDelegate<FileModel.Folder, FileModel.Folder, FolderItemHolder>() {
    override fun createViewHolder(parent: ViewGroup): FolderItemHolder {
        return FolderItemHolder(inflateLayout(R.layout.item_file_browser, parent))
    }

    override fun isForItemType(item: Any): Boolean {
        return item is FileModel.Folder
    }
}
