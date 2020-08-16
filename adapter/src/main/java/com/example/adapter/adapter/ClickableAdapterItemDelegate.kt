package com.deutschebahn.streckenagent2.ui.common.recycler

import android.view.View
import timber.log.Timber

abstract class ClickableAdapterItemDelegate<ItemType : Any, PayloadType : Any?, ItemHolder : DelegationAdapterItemHolder<ItemType>> :
    AdapterItemDelegate<ItemType,ItemHolder>() {

    /**
     * Set this to define a function taking [PayloadType] that is called when an item belonging to this delegate triggers something
     * (e.g. as a result of a click).
     */
    protected open val receiver: ((PayloadType) -> Unit)? = null

    /**
     * Override this if necessary. If not overriden, it forwards click events (when [isItemClickable] is true)
     * vial [mapToReceiverPayload] to the [receiver].
     */
    protected open fun doOnClick(view: View, item: ItemType) {
        mapToReceiverPayload(view, item)?.let {
            receiver?.invoke(it) ?: Timber.w(
                "ClickReceiver not set. Swallowing this click event")
        } ?: Timber.e("Could not map RecyclerItem to receiver payload properly!")
    }

    /**
     * Override to return something else than null!
     */
    protected open fun mapToReceiverPayload(view: View, item: ItemType): PayloadType? {
        return null
    }

    /**
     * This method hides away preparational work when the adapter's onBindViewHolder() is called, before it
     * calls [bindViewHolder] which is implemented in the respective subclass.
     *
     * The unchecked casts in here should be covered by the .isForItemType check.
     */
    @Suppress("Unchecked cast")
    override fun doBinding(item: Any, holder: DelegationAdapterItemHolder<*>) {
        if (this.isForItemType(item)) {
            holder.itemView.setOnClickListener { view -> doOnClick(view, item as ItemType) }
            bindViewHolder(item as ItemType, holder as ItemHolder)
        } else {
            onBindViewHolderFailed(item, holder)
        }
    }
}

