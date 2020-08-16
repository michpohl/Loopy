package com.deutschebahn.streckenagent2.ui.common.recycler

import android.view.ViewGroup
import timber.log.Timber

abstract class AdapterItemDelegate<I : Any, H : DelegationAdapterItemHolder<I>> {

    /**
     * Implement this with the necessary logic to return your desired ViewHolder subclass of [H].
     */
    abstract fun createViewHolder(parent: ViewGroup): H

    /**
     * Override this if you need to do more than just call bind() on your [DelegationAdapterItemHolder]
     * subclass and do any other necessary work (like setting the holder's receiver to {this.receiver(mapToReceiverPayload(it, item))}, if you don't
     * trigger it through a click event.
     */
    open fun bindViewHolder(item: I, holder: H) {
        holder.bind(item)
    }

    /**
     * Since we cannot check for the erased type of [I], implement this in your delegate class and check for the
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
            bindViewHolder(item as I, holder as H)
        } else {
            onBindViewHolderFailed(item, holder)
        }
    }

    protected fun onBindViewHolderFailed(item: Any, holder: DelegationAdapterItemHolder<*>) {
        Timber.w("Failed to bind ${item::class.java.canonicalName} and ${holder::class.java.canonicalName}")
    }
}

