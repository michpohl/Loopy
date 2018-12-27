package de.michaelpohl.loopy.ui.main.browser

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.FileType
import de.michaelpohl.loopy.databinding.ItemFileBrowserBinding
import hugo.weaving.DebugLog
import timber.log.Timber

@DebugLog
class FileBrowserAdapter(
    private val onSelectedItemsChangedListener: ((List<FileModel>) -> Unit),
    private val onItemClickedListener: ((FileModel) -> Unit)
) : RecyclerView.Adapter<FileBrowserItem>() {

    private var filesList = listOf<FileModel>()
        set (newList) {
            //first the folders, a-z, then the files, a-z
            field = newList.filter { it.fileType == FileType.FOLDER }.sortedWith(compareBy { it.name.toLowerCase() }) +
                    newList.filter { it.fileType == FileType.FILE }.sortedWith(compareBy { it.name.toLowerCase() })
        }
    var onItemSelectedListener: ((FileModel) -> Unit)? = null

    var selectedItems = mutableListOf<FileModel>()
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileBrowserItem {

        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemFileBrowserBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_file_browser, parent, false)
        return FileBrowserItem(
            parent.context,
            binding,
            filesList,
            onItemClickedListener,
            onItemSelectedListener
        )
    }

    override fun getItemCount() = filesList.size

    override fun onBindViewHolder(holder: FileBrowserItem, position: Int) {
        val fileViewModel = FileBrowserItemViewModel(
            position,
            filesList[position],
            this::onItemSelectedChanged,
            this::onItemClicked
        )
        if (selectedItems.contains(filesList[position])) {
            fileViewModel.selected.set(true)
        }

        holder.bind(fileViewModel)
        fileViewModel.update()
    }

    fun updateData(filesList: List<FileModel>) {
        this.filesList = filesList
        notifyDataSetChanged()
    }

    fun selectAll() {
        selectedItems.addAll(filesList)
        notifyDataSetChanged()
    }

    fun deselectAll() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    private fun onItemClicked(fileModel: FileModel) {
        Timber.d("Clicked on an item")
        onItemClickedListener.invoke(fileModel)
    }

    private fun onItemSelectedChanged(isSelected: Boolean, position: Int) {
        val fileModel = filesList[position]
        if (isSelected && !selectedItems.contains(filesList[position])) {
            Timber.d("Added file %s to the selected list", fileModel.name)
            selectedItems.add((fileModel))
        } else if (!isSelected && selectedItems.contains(filesList[position])) {
            selectedItems.remove((fileModel))
            Timber.d("Removed file %s from the selected list", fileModel.name)
        } else {
            //do nothing
            Timber.d("Did nothing")
        }
        Timber.d("Invoking with: %s", selectedItems)
        onSelectedItemsChangedListener.invoke(selectedItems)
    }
}