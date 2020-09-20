package de.michaelpohl.loopy.ui.main.settings.items

import android.view.View
import android.widget.TextView
import com.example.adapter.adapter.DelegationAdapterItemHolder
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.find
import kotlinx.android.synthetic.main.item_settings_checkable.view.*

class SettingsCheckableViewHolder(itemView: View) :

    DelegationAdapterItemHolder<SettingsItemModel.CheckableSetting>(itemView) {

    private lateinit var label: TextView

    override fun onCreated() {
        super.onCreated()
        label = itemView.find(R.id.tv_label)
    }

    override fun bind(item: SettingsItemModel.CheckableSetting) {
        label.text = item.label
    }
}
