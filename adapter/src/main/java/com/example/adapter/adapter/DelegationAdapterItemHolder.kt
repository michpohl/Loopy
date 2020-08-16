package com.deutschebahn.streckenagent2.ui.common.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Base class for ViewHolders that are used by a [DelegationAdapter]
 */
abstract class DelegationAdapterItemHolder <I: Any>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    open lateinit var receiver: ((View) -> Unit)

    abstract fun bind(item: I)
}
