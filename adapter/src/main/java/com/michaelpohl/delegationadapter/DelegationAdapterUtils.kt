package com.michaelpohl.delegationadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.michaelpohl.delegationadapter.AdapterItemDelegate
import com.michaelpohl.delegationadapter.AnyDiffCallback

/**
 * Builds a [DelegationAdapter] with the specs described in the lambda block.
 *
 * val myAdapter = adapter<ItemType> {
 *   delegates = listOf(
 *     Delegate1{ onClick -> myReceivingMethod(onclick)},
 *     Delegate2()
 *   )
 *   diffCallback = ItemType.DiffCallback()
 *   sorting = Sorting.Basic<ItemType>()
 * }
 *
 * delegates must be set, diffCallback and sorting can be null if you don't need it.
 *
 * Use [customAdapter] instead of [adapter] if you want to create an adapter with sorting of type [Sorting.Custom].
 * In that case you also have to specify the [UpdateType], which is the type of object the [CustomDelegationAdapter]
 * will expect as a parameter for its [update] function.
 * Usage:
 *  * val myAdapter = customAdapter<ItemType, UpdateType> {
 *   delegates = listOf(
 *     Delegate1{ onClick -> myReceivingMethod(onclick)},
 *     Delegate2()
 *   )
 *   diffCallback = ItemType.DiffCallback()
 *   sorting = Sorting.Custom<ItemType, UpdateType>()
 * }
 */
fun <ItemType : Any> adapter(block: DelegationAdapterBuilder<ItemType>.() -> Unit): DelegationAdapter<ItemType> {
    val builder = DelegationAdapterBuilder<ItemType>().apply(block)
    return builder.build()
}

fun <ItemType : Any, UpdateType : Any> customAdapter(block: CustomDelegationAdapterBuilder<ItemType, UpdateType>.() -> Unit)
        : CustomDelegationAdapter<ItemType, UpdateType> {
    val builder = CustomDelegationAdapterBuilder<ItemType, UpdateType>().apply(block)
    return builder.build()
}

class DelegationAdapterBuilder<ItemType : Any> {
    var diffCallback: DiffUtil.ItemCallback<ItemType>? = AnyDiffCallback<ItemType>()
    var sorting: Sorting.Basic<ItemType>? = null
    lateinit var delegates: List<AdapterItemDelegate<out ItemType, *>>
    fun build() = DelegationAdapter(diffCallback, sorting, delegates)
}

class CustomDelegationAdapterBuilder<ItemType : Any, UpdateType : Any> {
    var diffCallback: DiffUtil.ItemCallback<ItemType>? = AnyDiffCallback<ItemType>()
    lateinit var delegates: List<AdapterItemDelegate<out ItemType, *>>
    lateinit var sorting: Sorting.Custom<ItemType, UpdateType>
    fun build() = CustomDelegationAdapter(diffCallback, sorting, delegates)
}

/**
 * Returns all ViewHolders registered with this [RecyclerView] as a [Sequence]
 */
fun RecyclerView.viewHolders(): Sequence<RecyclerView.ViewHolder> {
    return this.children.map { child -> this.getChildViewHolder(child) }
}

/**
 * Helper to quickly inflate views for ViewHolders (and elsewhere)
 */
fun inflateLayout(layout: Int, parent: ViewGroup, attachToRoot: Boolean? = false): View =
    LayoutInflater.from(parent.context).inflate(layout, parent, attachToRoot!!)

/**
 * Helper to quickly inflate views for ViewHolders (and elsewhere)
 * Note the standard width and height settings! change them if needed.
 */
fun <T : ViewGroup> inflateView(
    view: T,
    widthParam: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    heightParam: Int = ViewGroup.LayoutParams.WRAP_CONTENT
): T {
    view.layoutParams = ViewGroup.LayoutParams(widthParam, heightParam)
    return view
}
