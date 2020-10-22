package de.michaelpohl.loopy.ui.main.settings.items

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import com.example.adapter.adapter.DelegationAdapterItemHolder
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.find

class SettingsCheckableViewHolder(itemView: View) :

    DelegationAdapterItemHolder<SettingsItemModel.CheckableSetting>(itemView) {

    private lateinit var label: TextView
    private lateinit var checkBox: CheckBox

    override fun onCreated() {
        super.onCreated()
        label = itemView.find(R.id.tv_label)
        checkBox = itemView.find(R.id.cb_checkbox)
    }

    override fun bind(item: SettingsItemModel.CheckableSetting) {
        label.text = item.label
        checkBox.isChecked = item.isChecked
    }
}
