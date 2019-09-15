package de.michaelpohl.loopy.ui.main.player

import android.content.Context

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.DialogHelper
import de.michaelpohl.loopy.databinding.ItemLoopBinding
import de.michaelpohl.loopy.model.DataRepository

class LoopsAdapter(
    val context: Context,
    private val onProgressChangedByUserListener: (Float) -> Unit
) : RecyclerView.Adapter<PlayerItem>() {

    private var loopsList = listOf<AudioModel>()
    var onItemSelectedListener: ((AudioModel, Int, PlayerItemViewModel.SelectionState) -> Unit)? = null
    private var onProgressUpdatedListener: ((Float) -> Unit)? = null
    private var onLoopsElapsedChangedListener: ((Int) -> Unit)? = null
    var selectedPosition = -1
    var preSelectedPosition = -1

    lateinit var dialogHelper: DialogHelper

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

        val itemViewModel = PlayerItemViewModel(
            position,
            loopsList[position],
            this::onItemClicked,
            this::onProgressChangedByUserListener.invoke(),
            this::onRemoveItemClicked
        )

        //TODO this should be done in the viewmodel, but I don't want to pass the context there. need to find a solution
        if (position == selectedPosition) {
            itemViewModel.backgroundColor = ContextCompat.getColor(context, R.color.action)
            itemViewModel.selectedState = PlayerItemViewModel.SelectionState.SELECTED

            //TODO again: DataRepository should have methods for this to make it nicer
            if (DataRepository.settings.showLoopCount) {
                itemViewModel.loopsCountVisibility.set(View.VISIBLE)
            } else {
                itemViewModel.loopsCountVisibility.set(View.GONE)

            }

            itemViewModel.canSeekAudio.set(true)
            onProgressUpdatedListener = { it: Float -> itemViewModel.updateProgress(it) }
            onLoopsElapsedChangedListener = { it -> itemViewModel.updateLoopsCount(it) }
        } else {
            if (position == preSelectedPosition) {
                itemViewModel.backgroundColor = ContextCompat.getColor(context, R.color.preselected_item)
                itemViewModel.selectedState = PlayerItemViewModel.SelectionState.PRESELECTED
            } else {
                itemViewModel.loopsCountVisibility.set(View.GONE)
                itemViewModel.backgroundColor = ContextCompat.getColor(context, R.color.content_background)
                itemViewModel.selectedState = PlayerItemViewModel.SelectionState.NOT_SELECTED
            }
        }

        holder.bind(itemViewModel)
    }

    fun updateData(newList: List<AudioModel>) {
        setLoopsList(newList)
        notifyDataSetChanged()
    }

    fun onLoopsElapsedChanged(loopsElapsed: Int) {
        onLoopsElapsedChangedListener?.invoke(loopsElapsed)
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

    private fun onItemClicked(position: Int, itemState: PlayerItemViewModel.SelectionState) {
        onItemSelectedListener?.invoke(loopsList[position], position, itemState)
    }

    private fun onRemoveItemClicked(position: Int) {
        dialogHelper.requestConfirmation(
            context.getString(R.string.dialog_remove_loop_header),
            context.getString(R.string.dialog_remove_loop_content)
        ) { removeItemFromLoopsList(position) }
    }

    fun removeItemFromLoopsList(itemPosition: Int) {
        val currentLoops = loopsList.toMutableList()
        currentLoops.removeAt(itemPosition)
        loopsList = currentLoops

        // if we delete an item that is higher up in the list we have to move the selected/ preselected
        // items up to make up for the missing item. otherwise the player gets confused.
        if (itemPosition < selectedPosition) selectedPosition -= 1
        if (itemPosition < preSelectedPosition) preSelectedPosition -= 1
        notifyDataSetChanged()
    }

    private fun setLoopsList(newList: List<AudioModel>) {
        loopsList = newList.sortedWith(compareBy { it.name.toLowerCase() })
        //TODO rebuild filtering with audioModels
//            .filter { it.isValidFileType() }
    }
}

