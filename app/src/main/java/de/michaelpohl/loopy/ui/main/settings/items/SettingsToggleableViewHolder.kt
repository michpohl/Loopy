package de.michaelpohl.loopy.ui.main.settings.items

import android.view.View
import android.widget.RadioButton
import android.widget.TextView
import com.example.adapter.adapter.DelegationAdapterItemHolder
import de.michaelpohl.loopy.R
import de.michaelpohl.loopy.common.find
import de.michaelpohl.loopy.ui.main.settings.items.SettingsItemModel.ToggleableSetting.Companion.ToggleState
import timber.log.Timber

class SettingsToggleableViewHolder(itemView: View) :
    DelegationAdapterItemHolder<SettingsItemModel.ToggleableSetting>(itemView) {
    private lateinit var firstLabel: TextView
    private lateinit var secondLabel: TextView
    private lateinit var firstRadioButton: RadioButton
    private lateinit var secondRadioButton: RadioButton

    override fun onCreated() {
        super.onCreated()
        firstLabel = itemView.find(R.id.tv_label_first)
        secondLabel = itemView.find(R.id.tv_label_second)
        firstRadioButton = itemView.find(R.id.rb_radio_first)
        secondRadioButton = itemView.find(R.id.rb_radio_second)

    }

    override fun bind(item: SettingsItemModel.ToggleableSetting) {
        firstLabel.text = item.label
        secondLabel.text = item.secondLabel
        firstRadioButton.isChecked = item.toggleState == ToggleState.FIRST
        secondRadioButton.isChecked = item.toggleState == ToggleState.SECOND

    }
}
