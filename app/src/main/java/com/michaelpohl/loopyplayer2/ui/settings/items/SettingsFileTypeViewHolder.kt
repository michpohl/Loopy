package com.michaelpohl.loopyplayer2.ui.settings.items

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.michaelpohl.delegationadapter.DelegationAdapterItemHolder
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.common.find

class SettingsFileTypeViewHolder(itemView: View) :

    DelegationAdapterItemHolder<SettingsItemModel.FileTypeSetting>(itemView) {

    private lateinit var label: TextView
    private lateinit var checkBox: CheckBox
    override fun onCreated() {
        super.onCreated()
        label = itemView.find(R.id.tv_label)
        checkBox = itemView.find(R.id.cb_checkbox)
    }

    override fun bind(item: SettingsItemModel.FileTypeSetting) {
        label.text = item.label
        checkBox.isChecked = item.isChecked
    }
}
