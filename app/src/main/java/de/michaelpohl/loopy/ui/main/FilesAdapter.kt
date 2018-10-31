package de.michaelpohl.loopy.ui.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel

class FilesAdapter : RecyclerView.Adapter<FileOrFolderItem>() {

    var onItemClickListener: ((FileModel) -> Unit)? = null

    var onItemSelectedListener: ((FileModel) -> Unit)? = null
    var filesList = listOf<FileModel>()
    var currentFilesList = listOf<FileModel>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileOrFolderItem {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recycler_file, parent, false)
        return FileOrFolderItem(view, filesList, onItemClickListener, onItemSelectedListener)
    }

    override fun getItemCount() = filesList.size

    override fun onBindViewHolder(holder: FileOrFolderItem, position: Int) = holder.bindView(position)

    fun updateData(filesList: List<FileModel>) {
        this.filesList = filesList
        notifyDataSetChanged()
    }


}