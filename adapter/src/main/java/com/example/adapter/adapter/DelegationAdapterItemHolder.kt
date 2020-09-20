package com.example.adapter.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Base class for ViewHolders that are used by a [DelegationAdapter]
 */
abstract class DelegationAdapterItemHolder <I: Any>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    open lateinit var toggleListener: ((View) -> Unit)

    open fun onCreated() {}
    abstract fun bind(item: I)
}
