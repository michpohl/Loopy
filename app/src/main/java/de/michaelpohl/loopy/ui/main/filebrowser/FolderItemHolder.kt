package de.michaelpohl.loopy.ui.main.filebrowser

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.deutschebahn.streckenagent2.ui.common.recycler.DelegationAdapterItemHolder
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.find
import de.michaelpohl.loopy.common.getDrawable

class FolderItemHolder(itemView: View) : ItemHolder<FileModel.Folder>(itemView) {
    override fun bind(item: FileModel.Folder) {
        label.text = item.name
        subLabel.text = "(${item.subFiles} files)" //TODO change to resource
        icon.setImageDrawable(getDrawable(R.drawable.ic_folder))
    }
}
