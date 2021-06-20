package com.michaelpohl.loopyplayer2.ui.settings.items

import android.content.res.Resources
import com.michaelpohl.loopyplayer2.model.AppStateRepository
import com.michaelpohl.loopyplayer2.ui.settings.AppSetting
import org.koin.core.KoinComponent
import org.koin.core.inject

sealed class SettingsItemModel : KoinComponent {

    abstract val setting: AppSetting
    abstract fun flip(): SettingsItemModel
    private val resources: Resources by inject()

    open val label: String by lazy {
        setting.displayNameResource?.let {
            resources.getString(it)
        } ?: ""
    }

    data class CheckableSetting(
        override val setting: AppSetting,
        val isChecked: Boolean
    ) : SettingsItemModel(), KoinComponent {

        override fun flip(): CheckableSetting {
            return this.copy(isChecked = !this.isChecked)
        }
    }

    data class FileTypeSetting(
        override val setting: AppSetting,
        val isChecked: Boolean,
        val type: AppStateRepository.Companion.AudioFileType
    ) : SettingsItemModel() {

        override fun flip(): FileTypeSetting {
            return this.copy(isChecked = !this.isChecked)
        }
    }

    data class MultipleChoiceSetting(
        override val setting: AppSetting,
        val choices: Set<SettingsChoice>
    ) : SettingsItemModel() {

        override fun flip(): MultipleChoiceSetting {
            return this
        }
    }

    data class Header(override val label: String) : SettingsItemModel() {

        override val setting: AppSetting = AppSetting.NONE
        override fun flip(): Header {
            /* do nothing */
            return this
        }
    }
}
typealias SettingsChoice = Pair<String, Boolean>

fun SettingsChoice.name() = this.first
fun SettingsChoice.isChecked() = this.second




