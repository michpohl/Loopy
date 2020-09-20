package de.michaelpohl.loopy.ui.main.settings.items

import android.view.View
import android.widget.TextView
import com.example.adapter.adapter.DelegationAdapterItemHolder
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.find

class SettingsToggleableViewHolder(itemView: View) :
    DelegationAdapterItemHolder<SettingsItemModel.ToggleableSetting>(itemView) {
    private lateinit var firstLabel: TextView
    private lateinit var secondLabel: TextView


    override fun onCreated() {
        super.onCreated()
        firstLabel = itemView.find(R.id.tv_label_first)
        secondLabel = itemView.find(R.id.tv_label_second)

    }

    override fun bind(item: SettingsItemModel.ToggleableSetting) {
        firstLabel.text = item.label
        secondLabel.text = item.secondLabel
    }
}
