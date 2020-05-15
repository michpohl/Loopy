package de.michaelpohl.loopy.ui.main.player

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.AudioModel
import de.michaelpohl.loopy.common.DialogHelper
import de.michaelpohl.loopy.databinding.ItemLoopBinding
import timber.log.Timber

class NewPlayerAdapter(
    private val onProgressChangedByUserListener: (Float) -> Unit

) : Adapter<NewPlayerItemHolder>() {

    private val holders = mutableListOf<NewPlayerItemHolder>()
    var items = listOf<AudioModel>()

    lateinit var dialogHelper: DialogHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewPlayerItemHolder {
        val binding: ItemLoopBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_loop, parent, false)
        val item = NewPlayerItemHolder(binding)
        holders.add(item)
        return item
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: NewPlayerItemHolder, position: Int) {
        val itemViewModel = NewPlayerItemViewModel(
            items[position],
            this::onItemClicked,
            this::onProgressChangedByUserListener.invoke(),
            this::onRemoveItemClicked
        )
        holder.bind(itemViewModel)
        // define the item's selection status, imho per name
    }

    private fun onItemClicked() {
        Timber.d("Clicked")
    }

    private fun onRemoveItemClicked() {
        Timber.d("OnRemoveClicked")
    }
}