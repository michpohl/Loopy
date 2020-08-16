package de.michaelpohl.loopy.ui.main.filebrowser

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.common.FileType
import de.michaelpohl.loopy.databinding.ItemFileBrowserBinding
import kotlinx.coroutines.selects.select
import timber.log.Timber
import java.util.*

//class FileBrowserAdapter(
//    private val onSelectedItemsChangedListener: ((List<FileModel>) -> Unit),
//    private val onItemClickedListener: ((FileModel) -> Unit)
//) : RecyclerView.Adapter<FileBrowserItem>() {
//
//    private var filesList = listOf<FileModel>()
//        set(newList) {
//            //sort them: first the folders, a-z, then the files, a-z
//            field = newList.filter { it.fileType == FileType.FOLDER }
//                .sortedWith(compareBy { it.name.toLowerCase(Locale.getDefault()) }) +
//                newList.filter { it.fileType == FileType.FILE }
//                    .sortedWith(compareBy { it.name.toLowerCase(Locale.getDefault()) })
//        }
//
//    var selectedItems = mutableListOf<FileModel>()
//        private set
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileBrowserItem {
//
//        val inflater = LayoutInflater.from(parent.context)
//        val binding: ItemFileBrowserBinding =
//            DataBindingUtil.inflate(inflater, R.layout.item_file_browser, parent, false)
//        return FileBrowserItem(
//            parent.context,
//            binding
//        )
//    }
//
//    override fun getItemCount() = filesList.size
//
//    override fun onBindViewHolder(holder: FileBrowserItem, position: Int) {
//
//        val fileViewModel = FileBrowserItemViewModel(
//            position,
//            filesList[position],
//            this::onItemSelectedChanged,
//            this::onItemClicked
//
//        )
//        if (selectedItems.contains(filesList[position])) {
//            fileViewModel.selected.set(true)
//        }
//
//        holder.bind(fileViewModel)
//        fileViewModel.update()
//    }
//
//    fun updateData(filesList: List<FileModel>) {
//        this.filesList = filesList
//        notifyDataSetChanged()
//    }
//
//    fun selectAll() {
//
//        //filtering folders out because this adds everything that is displayed which is wrong
//        selectedItems.addAll(filesList.filter { it.fileType == FileType.FILE })
//        notifyDataSetChanged()
//    }
//
//    fun deselectAll() {
//        selectedItems.clear()
//        notifyDataSetChanged()
//    }
//
//    private fun onItemClicked(fileModel: FileModel) {
//        Timber.d("On item clicked: $fileModel")
//        if (selectedItems.contains(fileModel)) {
//            selectedItems.remove(fileModel)
//        } else {
//            selectedItems.add(fileModel)
//        }
//        onSelectedItemsChangedListener.invoke(selectedItems)
//    }
//
//    private fun onItemSelectedChanged(isSelected: Boolean, position: Int) {
//        val fileModel = filesList[position]
//        if (isSelected && !selectedItems.contains(filesList[position])) {
//            selectedItems.add((fileModel))
//        } else if (!isSelected && selectedItems.contains(filesList[position])) {
//            selectedItems.remove((fileModel))
//        } else {
//            //do nothing
//        }
//        onSelectedItemsChangedListener.invoke(selectedItems)
//    }
//}
