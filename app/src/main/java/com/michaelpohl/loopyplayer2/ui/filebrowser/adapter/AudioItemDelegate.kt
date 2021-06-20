package com.michaelpohl.loopyplayer2.ui.filebrowser.adapter

import android.view.ViewGroup
import com.michaelpohl.delegationadapter.AdapterItemDelegate
import com.michaelpohl.delegationadapter.inflateLayout
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.common.FileModel

class AudioItemDelegate(val receiver: (FileModel.AudioFile) -> Unit) :
    AdapterItemDelegate<FileModel.AudioFile, AudioItemHolder>() {

    override fun createViewHolder(parent: ViewGroup): AudioItemHolder {
        val holder = AudioItemHolder(inflateLayout(R.layout.item_browser, parent))
        holder.onCheckedChangedReceiver = this.receiver
        return holder
    }

    override fun isForItemType(item: Any): Boolean {
        return item is FileModel.AudioFile
    }
}
