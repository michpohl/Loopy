package com.michaelpohl.delegationadapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.michaelpohl.delegationadapter.AdapterItemDelegate
import com.michaelpohl.delegationadapter.AnyDiffCallback
import timber.log.Timber

/**
 * A [RecyclerView.Adapter] that uses delegates to handle its internal logic, to make it more simple to have complex
 * lists with multiple view types.
 *
 * @param delegates A number of delegate classes that extend [AdapterItemDelegate]. Normally there is one delegate for
 * each view type the [RecyclerView] is supposed to be able to present.
 */
open class DelegationAdapter<ItemType : Any>(
    diffCallback: DiffUtil.ItemCallback<ItemType>? = AnyDiffCallback(),
    private val sorting: Sorting.Basic<ItemType>? = null,
    private val delegates: List<AdapterItemDelegate<out ItemType, *>>
) :
    ListAdapter<ItemType, DelegationAdapterItemHolder<*>>(diffCallback ?: AnyDiffCallback()) {

    protected lateinit var recyclerView: RecyclerView

    /**
     * The integer returned here is the responsible delegate's index in the delegate list, for view at the given position.
     */
    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        val chosen = delegates.find { it.isForItemType(item) }
        if (chosen == null) Timber.e("No delegate found for item of this type: ${item.javaClass.canonicalName}")
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
        return delegates[viewType].createViewHolder(parent).apply { onCreated() }
    }

    /**
     * The binding too, is handled by dhe delegate responsible for the corresponding item's type.
     */
    override fun onBindViewHolder(holder: DelegationAdapterItemHolder<*>, position: Int) {
        val item = getItem(position)

        val correctDelegate = delegates.find { it.isForItemType(item) }
        correctDelegate?.doBinding(item, holder)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    /**
     * Use this to update the adapter's current items. If a [Sorting] is registered, it will be used to check and
     * adjust the order of the items.
     */
    fun update(list: List<ItemType>) {
        sorting?.let {
            super.submitList(it.sort(list.toMutableList()))
        } ?: super.submitList(list)
    }
}
