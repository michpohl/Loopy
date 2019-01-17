package de.michaelpohl.loopy.ui.main.mediabrowser

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.AudioModel

//TODO rebuild for audioModels!

class MusicBrowserAdapter(
    private val onSelectedItemsChangedListener: ((List<AudioModel>) -> Unit),
    private val onItemClickedListener: ((AudioModel) -> Unit)
) : RecyclerView.Adapter<MusicBrowserItem>() {

    private var audioFilesList = listOf<AudioModel>()
        set (newList) {
            field = newList.sortedWith(compareBy { it.name })
        }

    var selectedItems = mutableListOf<AudioModel>()
        private set

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicBrowserItem {

        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemMusicBrowserBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_file_browser, parent, false)
        return MusicBrowserItem(
            parent.context,
            binding
        )
    }

    override fun getItemCount() = audioFilesList.size

    override fun onBindViewHolder(holder: MusicBrowserItem, position: Int) {

        val itemViewModel = MusicBrowserItemViewModel(
            position,
            audioFilesList[position],
            this::onItemSelectedChanged,
            this::onItemClicked

        )
        if (selectedItems.contains(audioFilesList[position])) {
            itemViewModel.selected.set(true)
        }

        holder.bind(itemViewModel)
        itemViewModel.update()
    }

    fun updateData(filesList: List<AudioModel>) {
        this.audioFilesList = filesList
        notifyDataSetChanged()
    }

    fun selectAll() {
        selectedItems.addAll(audioFilesList)
        notifyDataSetChanged()
    }

    fun deselectAll() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    private fun onItemClicked(audioModel: AudioModel) {
        onItemClickedListener.invoke(audioModel)
    }

    private fun onItemSelectedChanged(isSelected: Boolean, position: Int) {
        val audioModel = audioFilesList[position]
        if (isSelected && !selectedItems.contains(audioFilesList[position])) {
            selectedItems.add((audioModel))
        } else if (!isSelected && selectedItems.contains(audioFilesList[position])) {
            selectedItems.remove((audioModel))
        } else {
            //do nothing
        }
        onSelectedItemsChangedListener.invoke(selectedItems)
    }
}