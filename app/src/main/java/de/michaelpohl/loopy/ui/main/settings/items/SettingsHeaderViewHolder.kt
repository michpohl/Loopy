package de.michaelpohl.loopy.ui.main.settings.items

import android.view.View
import android.widget.TextView
import com.example.adapter.adapter.DelegationAdapterItemHolder
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.find

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
