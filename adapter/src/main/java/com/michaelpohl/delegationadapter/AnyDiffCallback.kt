package com.michaelpohl.delegationadapter

import androidx.recyclerview.widget.DiffUtil

/**
 * Fallback Callback for [DiffUtil] that compares two objects.
 * Note that this is the most basic implementation and might not lead to
 * correct results in all cases. If in doubt, create a callback class for
 * your specific use case (in 99% of cases you want your own)!
 */
class AnyDiffCallback<T : Any> : DiffUtil.ItemCallback<T>() {

    /**
     * Called to check whether two objects represent the same item.
     */
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }

    /**
     * Called to check whether two items have the same data.
     */
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return areItemsTheSame(oldItem, newItem)
    }
}
