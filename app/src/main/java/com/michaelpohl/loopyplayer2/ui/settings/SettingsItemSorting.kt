package com.michaelpohl.loopyplayer2.ui.settings

import android.content.res.Resources
import com.michaelpohl.delegationadapter.Sorting
import com.michaelpohl.loopyplayer2.R
import com.michaelpohl.loopyplayer2.ui.settings.items.SettingsItemModel
import org.koin.core.KoinComponent
import org.koin.core.inject

class SettingsItemSorting : Sorting.Custom<SettingsItemModel, SettingsViewModel.UIState>(), KoinComponent {

    private val resources: Resources by inject()
    override fun sort(input: SettingsViewModel.UIState): List<SettingsItemModel> {
        return input.settings.addHeaders()
    }

    private fun List<SettingsItemModel>.addHeaders(): List<SettingsItemModel> {
        val input = this.sortedBy { it.setting.ordinal }.toMutableList()
        val groups = mutableListOf<Int>()
        AppSetting.values()
            .sortedBy { it.group } // we want them sorted from 0 ascending
            .filter { it.group != -1 } // remove the "none" setting
            .forEach { setting ->
                val item = input.withIndex().find { it.value.setting == setting }
                item?.let {
                    if (!groups.contains(setting.group)) {
                        input.addHeaderFor(item.value.setting, item.index)
                        groups.add(setting.group)
                    }
                }
            }
        return input
    }

    private fun MutableList<SettingsItemModel>.addHeaderFor(setting: AppSetting, index: Int) {

        val string = when (setting.group) {
            0 -> resources.getString(R.string.settings_label_loop_switching_behaviour)
            1 -> resources.getString(R.string.settings_label_accepted_file_types)
            2 -> resources.getString(R.string.settings_item_sample_rate)
            3 -> resources.getString(R.string.settings_label_other_settings)
            else -> null
        }
        string?.let {
            val model = SettingsItemModel.Header(string)
            this.add(index, model)
        }
    }
}
