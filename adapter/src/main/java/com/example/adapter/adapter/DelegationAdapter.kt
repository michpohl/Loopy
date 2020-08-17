package com.deutschebahn.streckenagent2.ui.common.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.adapter.adapter.Sorting

/**
 * A [RecyclerView.Adapter] that uses delegates to handle its internal logic, to make it more simple to have complex
 * lists with multiple view types.
 *
 * @param delegates A number of delegate classes that extend [AdapterItemDelegate]. Normally there is one delegate for
 * each view type the [RecyclerView] is supposed to be able to present.
 */
open class DelegationAdapter<T : Any>(
    diffCallback: DiffUtil.ItemCallback<T>?,
    vararg delegates: AdapterItemDelegate<out T, *>
) :
    ListAdapter<T, DelegationAdapterItemHolder<*>>(diffCallback ?: AnyDiffCallback()) {

    private val delegates = delegates.toList()
    var sorting: Sorting<T>? = null

    /**
     * The integer returned here is the responsible delegate's index in the delegate list, for view at the given position.
     */
    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        val chosen = delegates.find { it.isForItemType(item) }
        return delegates.indexOf(chosen)
    }

    /**
     * ViewHolder creation gets delegated to the - wait for it - delegate.
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DelegationAdapterItemHolder<*> {
        if (viewType == -1) error("A viewType was requested for an item type that couldn't be found!")
        return delegates[viewType].createViewHolder(parent)
    }

    /**
     * The binding too, is handled by dhe delegate responsible for the corresponding item's type.
     */
    override fun onBindViewHolder(holder: DelegationAdapterItemHolder<*>, position: Int) {
        val item = getItem(position)

        val correctDelegate = delegates.find { it.isForItemType(item) }
        correctDelegate?.doBinding(item, holder)
    }

    fun update(list: List<T>) {
        sorting?.let {
            super.submitList(it.sort(list.toMutableList()))
        } ?: super.submitList(list)
    }

}
