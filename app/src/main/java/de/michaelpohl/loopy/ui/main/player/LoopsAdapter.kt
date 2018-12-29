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
import timber.log.Timber

class LoopsAdapter(var context: Context) : RecyclerView.Adapter<PlayerItem>() {


    private var loopsList = listOf<FileModel>()
    var onItemSelectedListener: ((FileModel, Int) -> Unit)? = null
    var onItemPreSelectedListener: ((FileModel, Int) -> Unit)? = null
    var onProgressUpdatedListener: ((Float) -> Unit)? = null
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

        val itemViewModel = PlayerItemViewModel(position, loopsList[position], this::onItemClicked)

        //TODO this should be done in the viewmodel, but I don't want to pass the context there. Find a solution
        if (position == selectedPosition) {
            itemViewModel.backgroundColor = ContextCompat.getColor(context, R.color.action)
            itemViewModel.state = PlayerItemViewModel.SelectionState.SELECTED
            onProgressUpdatedListener = {it: Float -> itemViewModel.updateProgress(it)}
        } else {
            if (position == preSelectedPosition) {
                itemViewModel.backgroundColor = ContextCompat.getColor(context, R.color.preselected_item)
                itemViewModel.state = PlayerItemViewModel.SelectionState.PRESELECTED
            } else {
                itemViewModel.backgroundColor = ContextCompat.getColor(context, R.color.content_background)
                itemViewModel.state = PlayerItemViewModel.SelectionState.NOT_SELECTED
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
        Timber.d("Progress: %s", position)
    }

    fun resetProgress() {
        updateProgress(0F)
    }

    fun onItemClicked(position: Int, itemState: PlayerItemViewModel.SelectionState) {
        Timber.d("Clicked on item with name: %s", loopsList[position])
        onItemSelectedListener?.invoke(loopsList[position], position)
    }

    private fun setLoopsList(newList: List<FileModel>) {
        loopsList = newList.sortedWith(compareBy { it.name.toLowerCase() }).filter { it.isValidFileType() }
        Timber.d("Adapter updating with these loops: ")
        loopsList.forEach { Timber.d("%s", it) }
    }
}

