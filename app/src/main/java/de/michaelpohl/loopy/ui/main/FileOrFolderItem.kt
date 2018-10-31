package de.michaelpohl.loopy.ui.main

import android.support.v7.widget.RecyclerView
import android.view.View
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileHelper
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.FileType
import kotlinx.android.synthetic.main.item_recycler_file.view.*

class FileOrFolderItem(
    itemView: View,
    filesList: List<FileModel>,
    onClickListener: ((FileModel) -> Unit)?,
    onSelectedListener: ((FileModel) -> Unit)?
) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private var filesList = filesList
    private var onItemClickListener = onClickListener
    private var onItemSelectedListener = onSelectedListener

    init {
        itemView.setOnClickListener(this)
        itemView.btn_pick_folder.setOnClickListener(this)
    }

    // not sure if this is the smartest way of doing this but it works
    override fun onClick(v: View?) {
        if (v!!.id == R.id.btn_pick_folder) {
            onItemSelectedListener?.invoke(filesList[adapterPosition])
        } else {
            onItemClickListener?.invoke(filesList[adapterPosition])
        }
    }

    fun bindView(position: Int) {
        val fileModel = filesList[position]
        itemView.nameTextView.text = fileModel.name

        if (fileModel.fileType == FileType.FOLDER) {
            itemView.folderTextView.visibility = View.VISIBLE
            itemView.totalSizeTextView.visibility = View.GONE
            itemView.folderTextView.text = "(${fileModel.subFiles} files)"
        } else {
            itemView.folderTextView.visibility = View.GONE
            itemView.totalSizeTextView.visibility = View.VISIBLE
            itemView.totalSizeTextView.text = "${String.format("%.2f", fileModel.sizeInMB)} mb"
        }
        if (!FileHelper.containsAudioFilesInAnySubFolders(fileModel.path)) {
            itemView.btn_pick_folder.visibility = View.GONE
        }

    }


}