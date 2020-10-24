package de.michaelpohl.loopy.ui.main.settings.items

import de.michaelpohl.loopy.model.AppStateRepository
import de.michaelpohl.loopy.ui.main.settings.AppSetting

sealed class SettingsItemModel {
    abstract val label: String
    abstract val setting: AppSetting
    abstract fun flip() : SettingsItemModel

    data class CheckableSetting(
        override val setting: AppSetting,
        override val label: String,
        val isChecked: Boolean
    ) : SettingsItemModel() {
        override fun flip(): SettingsItemModel.CheckableSetting {
            return this.copy(isChecked = !this.isChecked)
        }
    }

    data class FileTypeSetting(
        override val setting: AppSetting,
        override val label: String,
        val isChecked: Boolean,
        val type: AppStateRepository.Companion.AudioFileType
    ) : SettingsItemModel() {
        override fun flip(): SettingsItemModel.FileTypeSetting {
            return this.copy(isChecked = !this.isChecked)
        }
    }

    data class ToggleableSetting(
        override val setting: AppSetting,
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
        override val setting = AppSetting.NONE
        override fun flip(): SettingsItemModel.Header {
            /* do nothing */
            return this
        }
    }
}


