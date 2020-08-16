package de.michaelpohl.loopy.ui.main.filebrowser

import android.view.LayoutInflater
import android.view.ViewGroup
import com.deutschebahn.streckenagent2.ui.common.recycler.ClickableAdapterItemDelegate
import com.example.adapter.adapter.util.inflateLayout
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel

class FileBrowserItemDelegate(override val receiver: (FileModel) -> Unit) :
    ClickableAdapterItemDelegate<FileModel, FileModel, NewFileBroserItemHolder>() {
    override fun createViewHolder(parent: ViewGroup): NewFileBroserItemHolder {
        return NewFileBroserItemHolder(inflateLayout(R.layout.item_file_browser, parent))
    }

    override fun isForItemType(item: Any): Boolean {
        return item is FileModel
    }
}
