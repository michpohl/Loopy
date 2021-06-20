package com.michaelpohl.delegationadapter

import android.view.ViewGroup
import com.michaelpohl.delegationadapter.DelegationAdapterItemHolder
import timber.log.Timber

abstract class AdapterItemDelegate<ItemType : Any, HolderType : DelegationAdapterItemHolder<ItemType>> {

    /**
     * Implement this with the necessary logic to return your desired ViewHolder subclass of [HolderType].
     */
    abstract fun createViewHolder(parent: ViewGroup): HolderType

    /**
     * Override this if you need to do more than just call bind() on your [DelegationAdapterItemHolder]
     * subclass and do any other necessary work (like setting a listener etc, if you don't
     * trigger it through a click on the item.
     */
    open fun bindViewHolder(item: ItemType, holder: HolderType) {
        holder.bind(item)
    }

    /**
     * Since we cannot check for the erased type of [ItemType], implement this in your delegate class and check for the
     * correct item type.
     */
    abstract fun isForItemType(item: Any): Boolean

    /**
     * This method hides away preparational work when the adapter's onBindViewHolder() is called, before it
     * calls [bindViewHolder] which is implemented in the respective subclass.
     *
     * The unchecked casts in here should be covered by the .isForItemType check.
     */
    @Suppress("Unchecked cast")
    open fun doBinding(item: Any, holder: DelegationAdapterItemHolder<*>) {
        if (this.isForItemType(item)) {
            bindViewHolder(item as ItemType, holder as HolderType)
        } else {
            onBindViewHolderFailed(item, holder)
        }
    }

    protected fun onBindViewHolderFailed(item: Any, holder: DelegationAdapterItemHolder<*>) {
        Timber.w("Failed to bind ${item::class.java.canonicalName} and ${holder::class.java.canonicalName}")
    }
}

