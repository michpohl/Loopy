package com.michaelpohl.loopyplayer2.ui.settings.items

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.michaelpohl.delegationadapter.DelegationAdapterItemHolder
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.common.getColor

class SettingsMultipleChoiceViewHolder(itemView: View) :
    DelegationAdapterItemHolder<SettingsItemModel.MultipleChoiceSetting>(itemView) {

    override fun bind(item: SettingsItemModel.MultipleChoiceSetting) {
        this.item = item
        val choices = item.choices.toList()
        buildRadioGroup(choices)
    }

    private fun buildRadioGroup(choices: List<Pair<String, Boolean>>) {
        val buttonParams =
            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val groupParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val radioGroup = RadioGroup(itemView.context)
        choices.withIndex().forEach { pair ->
            radioGroup.addView(RadioButton(itemView.context).apply {
                text = pair.value.name()
                isChecked = pair.value.isChecked()
                id = pair.index
                layoutParams = buttonParams
                setTextColor(getColor(R.color.action))
                setBackgroundColor(getColor(R.color.window_background))

                layoutDirection = View.LAYOUT_DIRECTION_RTL
                setButtonDrawable(0) // for removing default drawable
                compoundDrawablePadding = 10
                setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    0,
                    R.drawable.radio_button,
                    0); // for adding drawable on the right
                gravity = Gravity.CENTER_VERTICAL
            })
        }
        with(radioGroup) {
            background = null
            setOnCheckedChangeListener { group, checkedId ->
                switchChecked(choices, checkedId)
            }
            layoutParams = groupParams

            (itemView as FrameLayout).apply {
                removeAllViews()
                addView(radioGroup)
            }
        }
    }

    private fun switchChecked(choices: List<Pair<String, Boolean>>, checkedId: Int) {
        val newChoices = choices.withIndex().map { it.value.copy(it.value.name(), it.index == checkedId) }
        item = item!!.copy(choices = newChoices.toSet())
        itemView.performClick()
    }
}
