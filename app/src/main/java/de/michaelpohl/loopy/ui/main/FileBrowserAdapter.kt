package de.michaelpohl.loopy.ui.main

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.databinding.ItemFileBrowserBinding
import hugo.weaving.DebugLog

@DebugLog
class FileBrowserAdapter : RecyclerView.Adapter<FileBrowserItem>() {

    var onItemClickListener: ((FileModel) -> Unit)? = null

    var onItemSelectedListener: ((FileModel) -> Unit)? = null
    private var filesList = listOf<FileModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileBrowserItem {

        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemFileBrowserBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_file_browser, parent, false)
        return FileBrowserItem(parent.context, binding, filesList, onItemClickListener, onItemSelectedListener)
    }

    override fun getItemCount() = filesList.size

    override fun onBindViewHolder(holder: FileBrowserItem, position: Int) {
        val vm = FileBrowserItemViewModel()
        vm.fileModel = filesList[position]
        holder.bind(vm)
        vm.update()
    }

    fun updateData(filesList: List<FileModel>) {
        this.filesList = filesList
        notifyDataSetChanged()
    }
}