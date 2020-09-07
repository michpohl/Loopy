package com.example.adapter.adapter

import android.view.View
import com.deutschebahn.streckenagent2.ui.common.recycler.DelegationAdapterItemHolder
import timber.log.Timber

abstract class ClickableAdapterItemDelegate<ItemType : Any, ItemHolder : DelegationAdapterItemHolder<ItemType>> :
    AdapterItemDelegate<ItemType, ItemHolder>() {

    /**
     * Set this to define a function taking [PayloadType] that is called when an item belonging to this delegate triggers something
     * (e.g. as a result of a click).
     */
    protected open val receiver: ((ItemType) -> Unit)? = null

    /**
     * Override this if necessary. If not overriden, it forwards click events (when [isItemClickable] is true)
     * to the [receiver].
     */
    protected open fun doOnClick(view: View, item: ItemType) {
        receiver?.invoke(item) ?: Timber.w(
            "ClickReceiver not set. Swallowing this click event"
        )
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

