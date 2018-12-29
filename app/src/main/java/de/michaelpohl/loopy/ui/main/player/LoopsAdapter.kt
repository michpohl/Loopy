package de.michaelpohl.loopy.ui.main.player

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.FileModel
import de.michaelpohl.loopy.databinding.ItemLoopBinding
import de.michaelpohl.loopy.ui.main.player.PlayerItemViewModel.SelectionState
import timber.log.Timber

class LoopsAdapter(var context: Context) : RecyclerView.Adapter<PlayerItem>() {

    private var loopsList = listOf<FileModel>()
    var onItemSelectedListener: ((FileModel, Int, SelectionState) -> Unit)? = null
    private var onProgressUpdatedListener: ((Float) -> Unit)? = null
    var selectedPosition = -1
    var preSelectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerItem {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemLoopBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_loop, parent, false)
        return PlayerItem(
            parent.context,
            binding
        )
    }

    override fun getItemCount() = loopsList.size

    override fun onBindViewHolder(holder: PlayerItem, position: Int) {
        Timber.d(
            "OnBindViewHolder, position: %s, selected position: %s , preselected position: %s, name: %s",
            position,
            selectedPosition,
            preSelectedPosition,
            loopsList[position].name
        )
        val itemViewModel = PlayerItemViewModel(position, loopsList[position], this::onItemClicked)

        //TODO this should be done in the viewmodel, but I don't want to pass the context there. Find a solution
        if (position == selectedPosition) {
            itemViewModel.backgroundColor = ContextCompat.getColor(context, R.color.action)
            itemViewModel.selectedState = PlayerItemViewModel.SelectionState.SELECTED
            onProgressUpdatedListener = { it: Float -> itemViewModel.updateProgress(it) }
        } else {
            if (position == preSelectedPosition) {
                itemViewModel.backgroundColor = ContextCompat.getColor(context, R.color.preselected_item)
                itemViewModel.selectedState = SelectionState.PRESELECTED
            } else {
                itemViewModel.backgroundColor = ContextCompat.getColor(context, R.color.content_background)
                itemViewModel.selectedState = PlayerItemViewModel.SelectionState.NOT_SELECTED
            }
        }

        holder.bind(itemViewModel)
    }

    fun updateData(newList: List<FileModel>) {
        setLoopsList(newList)
        notifyDataSetChanged()
    }

    fun updateProgress(position: Float) {
        onProgressUpdatedListener?.invoke(position)
    }

    fun resetProgress() {
        updateProgress(0F)
    }

    fun resetPreSelection() {
        val currentPreSelection = preSelectedPosition
        preSelectedPosition = -1
        notifyItemChanged(currentPreSelection)
    }

    fun notifyMultipleItems(positions: Array<Int>) {
        positions.forEach { notifyItemChanged(it) }
    }

    private fun onItemClicked(position: Int, itemState: SelectionState) {
        onItemSelectedListener?.invoke(loopsList[position], position, itemState)
    }

    private fun setLoopsList(newList: List<FileModel>) {
        loopsList = newList.sortedWith(compareBy { it.name.toLowerCase() }).filter { it.isValidFileType() }
        loopsList.forEach { Timber.d("%s", it) }
    }
}

