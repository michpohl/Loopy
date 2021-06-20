package com.michaelpohl.loopyplayer2.ui.settings.items

import android.view.View
import android.widget.TextView
import com.michaelpohl.delegationadapter.DelegationAdapterItemHolder
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.common.find

class SettingsHeaderViewHolder(itemView: View) :
    DelegationAdapterItemHolder<SettingsItemModel.Header>(itemView) {

    private lateinit var label: TextView
    override fun onCreated() {
        super.onCreated()
        label = itemView.find(R.id.tv_header_label)
    }

    override fun bind(item: SettingsItemModel.Header) {
        label.text = item.label
    }
}
