package de.michaelpohl.loopy.ui.main.settings.items

import de.michaelpohl.loopy.ui.main.settings.AppSetting

sealed class SettingsItemModel {
    abstract val label: String
    abstract fun flip() : SettingsItemModel

    data class CheckableSetting(
        val setting: AppSetting,
        override val label: String,
        val isChecked: Boolean
    ) : SettingsItemModel() {
        override fun flip(): SettingsItemModel.CheckableSetting {
            return this.copy(isChecked = !this.isChecked)
        }
    }

    data class ToggleableSetting(
        val setting: AppSetting,
        override val label: String,
        val secondLabel: String,
        val toggleState: ToggleState
    ) : SettingsItemModel() {

        override fun flip(): SettingsItemModel.ToggleableSetting {
            return this.copy(toggleState = if (this.toggleState == ToggleState.FIRST) ToggleState.SECOND else ToggleState.FIRST)
        }

        companion object {
            enum class ToggleState {
                FIRST, SECOND
            }
        }
    }

    data class Header(
        override val label: String
    ) : SettingsItemModel() {
        override fun flip(): SettingsItemModel.Header {
            /* do nothing */
            return this
        }
    }
}
