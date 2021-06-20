package com.michaelpohl.delegationadapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Base class for ViewHolders that are used by a [DelegationAdapter]
 */
abstract class DelegationAdapterItemHolder<I : Any>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    open lateinit var toggleListener: ((View) -> Unit)

    /**
     * Override this to store edited versions of your item. They will then be used by the clickListenenr
     */
    open var item: I? = null

    open fun onCreated() {}
    abstract fun bind(item: I)
}
