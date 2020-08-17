package de.michaelpohl.loopy.ui.main.filebrowser

import android.view.View
import android.view.ViewGroup
import com.deutschebahn.streckenagent2.ui.common.recycler.AdapterItemDelegate
import com.deutschebahn.streckenagent2.ui.common.recycler.ClickableAdapterItemDelegate
import com.example.adapter.adapter.util.inflateLayout
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel

class AudioItemDelegate( val receiver: (FileModel.AudioFile, Boolean) -> Unit) :
    AdapterItemDelegate<FileModel.AudioFile, AudioItemHolder>() {
    override fun createViewHolder(parent: ViewGroup): AudioItemHolder {
        val holder = AudioItemHolder(inflateLayout(R.layout.item_browser, parent))
        holder.onCheckedChangedReceiver = this.receiver
        return holder
    }

    override fun isForItemType(item: Any): Boolean {
        return item is FileModel.AudioFile
    }

    fun mapToReceiverPayload(view: View, item: FileModel.AudioFile): FileModel.AudioFile? {
        return item
    }
}
