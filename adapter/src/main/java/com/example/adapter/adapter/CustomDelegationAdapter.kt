package com.example.adapter.adapter

import androidx.recyclerview.widget.DiffUtil

/**
 * A [DelegationAdapter] which can take a custom [UpdateType] as input of it's [update] method instead of a plain
 * list of [ItemType]. Use in combination with [Sorting.Custom].
 */
open class CustomDelegationAdapter<ItemType : Any, UpdateType : Any>(
    diffCallback: DiffUtil.ItemCallback<ItemType>? = AnyDiffCallback(),
    private val sorting: Sorting.Custom<ItemType, UpdateType>,
    delegates: List<AdapterItemDelegate<out ItemType, *>>) :
    DelegationAdapter<ItemType>(diffCallback, null, delegates) {

    open fun update(input: UpdateType) {
        update(sorting.sort(input))
    }
}
