package de.michaelpohl.loopy.ui.main.settings.items

import de.michaelpohl.loopy.ui.main.settings.AppSetting

sealed class SettingsItemModel {
    abstract val label: String

    data class CheckableSetting(
        val setting: AppSetting,
        override val label: String,
        val isChecked: Boolean
    ) : SettingsItemModel()

    data class ToggleableSetting(
        val setting: AppSetting,
        override val label: String,
        val secondLabel: String,
        val toggleState: ToggleState
    ) : SettingsItemModel() {
        companion object {
            enum class ToggleState {
                FIRST, SECOND
            }
        }
    }

    data class Header(
        override val label: String
    ) : SettingsItemModel()
}
