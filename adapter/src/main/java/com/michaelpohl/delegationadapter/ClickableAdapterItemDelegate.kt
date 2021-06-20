package com.michaelpohl.delegationadapter

import android.view.View
import com.michaelpohl.delegationadapter.AdapterItemDelegate
import timber.log.Timber

/**
 * Delegate base class for recycler items that should be clickable (as in: click the entire item to trigger something).
 * Just set the clickListener, and it will automatically be connected to the item. If you need more complicated actions,
 * it's better to unse a regular [AdapterItemDelegate] and do the binding yourself in the [DelegationAdapterItemHolder]'s
 * [bind] method.
 */
abstract class ClickableAdapterItemDelegate<ItemType : Any, HolderType : DelegationAdapterItemHolder<ItemType>> :
    AdapterItemDelegate<ItemType, HolderType>() {

    /**
     * Set this to define a function taking [ItemType] that is called when an item belonging to this delegate triggers
     * something (e.g. as a result of a click).
     *
     * If you need to run any logic on the data sent to the listener, it's best applied in the delegate.
     */
    abstract val clickListener: ((ItemType) -> Unit)?

    /**
     * Override this if necessary. If not overriden, it forwards click events (when [isItemClickable] is true)
     * to the [clickListener].
     */
    protected open fun doOnClick(view: View, item: ItemType) {
        clickListener?.invoke(item) ?: Timber.w(
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
            bindViewHolder(item as ItemType, holder as HolderType)
        } else {
            onBindViewHolderFailed(item, holder)
        }
    }
}

