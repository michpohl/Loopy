package de.michaelpohl.loopy.ui.main.filebrowser

import android.view.ViewGroup
import com.deutschebahn.streckenagent2.ui.common.recycler.ClickableAdapterItemDelegate
import com.example.adapter.adapter.util.inflateLayout
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel

class AudioItemDelegate(override val receiver: (FileModel.AudioFile) -> Unit) :
    ClickableAdapterItemDelegate<FileModel.AudioFile, FileModel.AudioFile, AudioItemHolder>() {
    override fun createViewHolder(parent: ViewGroup): AudioItemHolder {
        return AudioItemHolder(inflateLayout(R.layout.item_file_browser, parent))
    }

    override fun isForItemType(item: Any): Boolean {
        return item is FileModel.AudioFile
    }
}
