package de.michaelpohl.loopy.ui.main

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel

class FileBrowserAdapter : RecyclerView.Adapter<FileBrowserItem>() {

    var onItemClickListener: ((FileModel) -> Unit)? = null

    var onItemSelectedListener: ((FileModel) -> Unit)? = null
    var filesList = listOf<FileModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileBrowserItem {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_file_browser, parent, false)
        var holder = FileBrowserItem(view, filesList, onItemClickListener, onItemSelectedListener)
        return holder
    }

    override fun getItemCount() = filesList.size

    override fun onBindViewHolder(holder: FileBrowserItem, position: Int) = holder.bindView(position)

    fun updateData(filesList: List<FileModel>) {
        this.filesList = filesList
        notifyDataSetChanged()
    }
}